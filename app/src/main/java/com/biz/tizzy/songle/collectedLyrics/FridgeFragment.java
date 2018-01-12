package com.biz.tizzy.songle.collectedLyrics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biz.tizzy.songle.TimesUpFragment;
import com.biz.tizzy.songle.guess.CorrectGuessFragment;
import com.biz.tizzy.songle.guess.GuessFragment;
import com.biz.tizzy.songle.guess.HalfCorrectFragment;
import com.biz.tizzy.songle.map.MapActivity;
import com.biz.tizzy.songle.LyricsSingleton;
import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.Song;
import com.biz.tizzy.songle.SongPreferences;
import com.biz.tizzy.songle.guess.WrongGuessFragment;

import java.util.concurrent.TimeUnit;

/**
 * Created by tizzy on 11/13/17.
 */

public class FridgeFragment extends Fragment {

    private static final String TAG = "FridgeFragment";

    private static final String ARG_SCREEN_ID = "screen_id";
    private static final String ARG_TIME = "time";
    private static final String DIALOG_GUESS = "DialogGuess";
    private static final String DIALOG_CORRECT_GUESS = "DialogCorrectGuess";
    private static final String DIALOG_WRONG_GUESS = "DialogWrongGuess";
    private static final String DIALOG_HALF_CORRECT = "DialogHalfCorrectGuess";
    private static final String DIALOG_TIMES_UP = "DialogTimesUp";
    private static final int REQUEST_GUESS = 0;

    static int mScreenNumber;
    public FridgeScreen mScreen;
    private static int mAllowedTime;
    private static int mTimeRemaining;
    public Song mSong;

    public static FridgeFragment newInstance(int id, int time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCREEN_ID, id);
        args.putInt(ARG_TIME, time);

        FridgeFragment fragment = new FridgeFragment();
        fragment.setArguments(args);

        mScreenNumber = id;
        mAllowedTime = time;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int screenId = (int) getArguments().getSerializable(ARG_SCREEN_ID);
            // gets called twice (for first two screens)
        //mScreen = ThreeScreens.get(getActivity()).getScreen(screenId);
        mScreen = LyricsSingleton.get(getActivity()).getScreen(screenId);

        //mSong = MapFragment.getSong();
        mSong = SongPreferences.getSong(getActivity());

        mAllowedTime = getArguments().getInt(ARG_TIME);
        new CountDownTimer(mAllowedTime, 1000) {

            public void onTick(long millisUntilFinished) {
                int mins = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                int secs = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - (mins*60);
                String displayTime = String.format("%02d : %02d", mins, secs);

                if (getActivity() == null) {
                    Log.d(TAG, "Activity context is null");
                } else {
                    Toast.makeText(getContext(), displayTime, Toast.LENGTH_LONG).show();
                }

                mTimeRemaining = (int) millisUntilFinished;
            }

            public void onFinish() {
                // GAME OVER
                FragmentManager manager = getFragmentManager();
                TimesUpFragment dialog = new TimesUpFragment();
                dialog.show(manager, DIALOG_TIMES_UP);
            }

        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflates BoxDrawingView
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        v.setBackgroundColor(mScreen.getBackground());
        return v;
    }

// GUESSING
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_GUESS) {
            String title = (String) data.getSerializableExtra(GuessFragment.EXTRA_TITLE);
            String artist = (String) data.getSerializableExtra(GuessFragment.EXTRA_ARTIST);

            FragmentManager manager = getFragmentManager();

            switch (checkAnswer(title, artist)) {
                case 1:
                    CorrectGuessFragment correctDialog = new CorrectGuessFragment();
                    correctDialog.show(manager, DIALOG_CORRECT_GUESS);
                    break;
                case 2:
                    HalfCorrectFragment halfDialog = new HalfCorrectFragment();
                    halfDialog.show(manager, DIALOG_HALF_CORRECT);
                    break;
                case 3:
                    WrongGuessFragment wrongDialog = new WrongGuessFragment();
                    wrongDialog.show(manager, DIALOG_WRONG_GUESS);
                    break;
            }
        }
    }

// TOOLBAR
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        // Update the menu button
        MenuItem searchItem = menu.findItem(R.id.map_view);
        searchItem.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guess:
                FragmentManager manager = getFragmentManager();
                //GuessFragment dialog = GuessFragment.newInstance(mSong.getTitle(), mSong.getArtist());
                GuessFragment dialog = new GuessFragment();
                dialog.setTargetFragment(FridgeFragment.this, REQUEST_GUESS);
                dialog.show(manager, DIALOG_GUESS);
                return true;
            case R.id.map_view:
                Intent intent = MapActivity.returnIntent(getActivity(), mTimeRemaining);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

// GUESSING
    private int checkAnswer(String title, String artist) {
        // capital vs lowercase doesn't matter
        boolean titleCorrect = title.equals(mSong.getTitle().toLowerCase());
        boolean artistCorrect = artist.equals(mSong.getArtist().toLowerCase());

        if (titleCorrect && artistCorrect) {
            return 1;
        } else {
            if (titleCorrect || artistCorrect) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
