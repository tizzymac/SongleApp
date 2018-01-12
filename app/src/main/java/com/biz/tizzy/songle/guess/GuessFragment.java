package com.biz.tizzy.songle.guess;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.setup.StartActivity;

/**
 * Created by tizzy on 11/18/17.
 */

public class GuessFragment extends DialogFragment {

    public static final String EXTRA_TITLE = "com.biz.tizzy.locatr2.title";
    public static final String EXTRA_ARTIST = "com.biz.tizzy.locatr2.artist";

    private static final String DIALOG_GIVE_UP = "DialogGiveUp";

    private static Button mGuessButton;
    private static Button mGiveUpButton;
    private EditText mTitleInput;
    private EditText mArtistInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_guess, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mTitleInput = (EditText) v.findViewById(R.id.title_input);
        mArtistInput = (EditText) v.findViewById(R.id.artist_input);

        mGuessButton = (Button) v.findViewById(R.id.guess);
        mGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(Activity.RESULT_OK, mTitleInput.getText().toString(), mArtistInput.getText().toString());
                alert.cancel();
            }
        });

        mGiveUpButton = (Button) v.findViewById(R.id.give_up);
        mGiveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // make sure they really want to quit
                FragmentManager manager = getFragmentManager();
                GiveUpFragment dialog = new GiveUpFragment();
                dialog.show(manager, DIALOG_GIVE_UP);
            }
        });

        return alert;
    }

    private void sendResult(int resultCode, String title, String artist) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_ARTIST, artist);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
