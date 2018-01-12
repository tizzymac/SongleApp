package com.biz.tizzy.songle.map;

/**
 * Created by tizzy on 10/30/17.
 */

public class MapPoint {
    private double mLat;
    private double mLon;
    private String mLyric;          // eg landslide
    private String mLyricLocation;  // eg 2:4
    private int mInterest;
            // 0 = unclassified
            // 1 = boring
            // 2 = notboring
            // 3 = interesting
            // 4 = veryinteresting

    public MapPoint(double lat, double lon) {
        mLat = lat;
        mLon = lon;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public int getInterest() {
        return mInterest;
    }

    public void setInterest(String interest) {
        switch (interest) {
            case "unclassified":
                this.mInterest = 0;
                break;
            case "boring":
                this.mInterest = 1;
                break;
            case "notboring":
                this.mInterest = 2;
                break;
            case "interesting":
                this.mInterest = 3;
                break;
            case "veryinteresting":
                this.mInterest = 4;
                break;
        }
    }

    public void setInterest(int interest) {
        // check is a legit number
        int[] interestLevels = {0, 1, 2, 3, 4};

        if (contains(interestLevels, interest)) {
            this.mInterest = interest;
        }
    }

    public String getLyric() {
        return mLyric;
    }

    public void setLyric(String lyric) {
        mLyric = lyric;
    }

    public String getLyricLocation() {
        return mLyricLocation;
    }

    public void setLyricLocation(String lyricLocation) {
        mLyricLocation = lyricLocation;
    }

    private static boolean contains(int[] array, int number) {
        for (int n : array) {
            if (n == number) {
                return true;
            }
        }
        return false;
    }
}
