package com.biz.tizzy.songle.setup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.biz.tizzy.songle.NoInternetFragment;
import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.Song;
import com.biz.tizzy.songle.SongPreferences;
import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by tizzy on 11/20/17.
 *
 * gif : https://www.youtube.com/watch?v=Mas3Lnd3WtM
 */

public class StartMenuFragment extends Fragment{

    private static final String TAG = "StartMenuFragment";
    private static final String DIALOG_NOINTERNET = "DialogNoInternet";
    private static final String DIALOG_NONEWSONG = "DialogNoNewSong";
    private static final String DIALOG_LEVEL = "DialogLevel";
    private static final int REQUEST_LEVEL = 0;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private Button mStartButton;
    public String mDocString;
    public String mLyricsDocString;
    private String mLevel;
    private int mTime;
    private GifImageView mGifView;

    // Song
    private String mSongNum;
    private Document mSongDoc;
    private String mArtist;
    private String mTitle;
    private Song mSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_menu, container, false);

        // Title gif
        mGifView = (GifImageView) view.findViewById(R.id.gif);
        try {
            InputStream inputStream = getContext().getAssets().open("songle_title.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            mGifView.setBytes(bytes);
            mGifView.startAnimation();
        } catch (IOException ex) {
           Log.e(TAG, "Error getting title gif: " + ex.getMessage());
        }

        // set up song
        mSongNum = SongPreferences.getSongNum(getActivity());
        String songDataURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
        new StartMenuFragment.GetSongDetails().execute(songDataURL);
        mLyricsDocString = getLyricsForSong();

        mStartButton = (Button) view.findViewById(R.id.new_game);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for location permission
                int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                // game won't start without location permission
                } else {

                    // Check if out of new songs
                    if (mSong == null) {
                        Log.d(TAG, "No new songs");
                        FragmentManager manager = getFragmentManager();
                        NoNewSongFragment dialog = new NoNewSongFragment();
                        dialog.show(manager, DIALOG_NONEWSONG);

                        // try to load song again
                        String songDataURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml";
                        new StartMenuFragment.GetSongDetails().execute(songDataURL);

                    } else {
                        // start level picker
                        FragmentManager manager = getFragmentManager();
                        LevelPickerFragment dialog = LevelPickerFragment.newInstance(mLevel);
                        dialog.setTargetFragment(StartMenuFragment.this, REQUEST_LEVEL);
                        dialog.show(manager, DIALOG_LEVEL);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_LEVEL) {
            mLevel = (String) data.getSerializableExtra(LevelPickerFragment.EXTRA_LEVEL);
            mTime = getTimeAllowed(mLevel);
            mDocString = getMapForLevel(mLevel);
            startLevel(mLevel, mTime, mDocString, mLyricsDocString);
        }
    }

    private void startLevel(String level, int time, String docString, String lyricsDocString) {

        Intent intent = SetUpActivity.newIntent(getActivity(), level, time, docString, lyricsDocString);
        startActivity(intent);
    }

    private String getMapForLevel(String level) {
        //String mapURL = getSong();
        String mapURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + mSongNum;

        switch (level) {
            case "Level1" :
                mapURL = mapURL + "/map5.kml";
                break;
            case "Level2":
                mapURL = mapURL + "/map4.kml";
                break;
            case "Level3":
                mapURL = mapURL + "/map3.kml";
                break;
            case "Level4":
                mapURL = mapURL + "/map2.kml";
                break;
            case "Level5":
                mapURL = mapURL + "/map1.kml";
                break;
            default:
                mapURL = null;
                break;
        }
        return mapURL;
    }

    private String getLyricsForSong() {

        String lyricsURL = "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/" + mSongNum + "/words.txt";

        return lyricsURL;
    }

    private String[] getArtistTitle() {

        if (mSongDoc != null) {
            NodeList listOfSongs = mSongDoc.getElementsByTagName("Song");
            String[] artist_title = SongDatabaseParser.getSongDetails(mSongNum, listOfSongs);
            return artist_title;
        } else {
            FragmentManager manager = getFragmentManager();
            NoInternetFragment dialog = new NoInternetFragment();
            dialog.show(manager, DIALOG_NOINTERNET);
            return null;
        }
    }

    private int getTimeAllowed(String level) {
        int time;
        switch (level) {
            case "Level1" :
                time = 30 * 60 * 1000;
                break;
            case "Level2" :
                time = 25 * 60 * 1000;
                break;
            case "Level3" :
                time = 20 * 60 * 1000;
                break;
            case "Level4" :
                time = 15 * 60 * 1000;
                break;
            case "Level5" :
                time = 10 * 60 * 1000;
                break;
            default:
                time = 0;
                break;
        }
        return time;
    }

    private class GetSongDetails extends AsyncTask<String,Void,Void> {
        private Document mDocument;
        private String mDocString;

        @Override
        protected Void doInBackground(String... strings) {
            mDocString = strings[0];
            try {
                Log.i(TAG, "Getting details from song database");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringComments(true);
                factory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                mDocument = builder.parse(new InputSource(mDocString));
            }
            catch (Exception ex) {
                Log.i(TAG, "Unable to get song details", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mDocument != null) {
                mSongDoc = mDocument;

                mArtist = getArtistTitle()[0];
                mTitle = getArtistTitle()[1];

                if (mArtist == null) {
                    // we've run out of songs
                    FragmentManager manager = getFragmentManager();
                    NoNewSongFragment dialog = new NoNewSongFragment();
                    dialog.show(manager, DIALOG_NONEWSONG);
                } else {
                    mSong = new Song(mArtist, mTitle);
                    SongPreferences.setSong(getActivity(), mSong);
                }

            } else {
                FragmentManager manager = getFragmentManager();
                NoInternetFragment dialog = new NoInternetFragment();
                dialog.show(manager, DIALOG_NOINTERNET);
            }
        }
    }
}
