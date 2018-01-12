package com.biz.tizzy.songle;

/**
 * Created by tizzy on 11/18/17.
 */

public class Song {
    private String mTitle;
    private String mArtist;

    public Song(String title, String artist) {
        mArtist = artist;
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }
}
