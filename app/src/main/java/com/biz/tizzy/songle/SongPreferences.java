package com.biz.tizzy.songle;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by tizzy on 11/21/17.
 */

public class SongPreferences {

    private static final String PREF_ARTIST = "artist";
    private static final String PREF_TITLE = "title";
    private static final String PREF_SONG_NUM = "song_num";

    public static Song getSong(Context context) {
        String artist;
        String title;

        artist = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ARTIST, null);
        title = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TITLE, null);

        Song song = new Song(artist, title);
        return song;
    }

    public static void setSong(Context context, Song song) {
        String artist = song.getArtist();
        String title = song.getTitle();

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_ARTIST, artist)
                .putString(PREF_TITLE, title)
                .apply();

        // set next song number
        String prevNum = getSongNum(context);
        String songNum = String.format("%02d", Integer.parseInt(prevNum)+1);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SONG_NUM, songNum)
                .apply();
    }

    public static String getSongNum(Context context) {
        String songNum = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SONG_NUM, null);
        if (songNum == null) {
            songNum = "01";
        }
        return songNum;
    }
}
