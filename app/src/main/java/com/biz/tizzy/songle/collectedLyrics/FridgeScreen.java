package com.biz.tizzy.songle.collectedLyrics;

import com.biz.tizzy.songle.collectedLyrics.Box;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tizzy on 11/13/17.
 */

public class FridgeScreen {

    private int mID;
    private int mBackground;
    private List<Box> mMagnets;

    public FridgeScreen(int id) {
        mID = id;
        switch (id) {
            case 0: mBackground = 0xffffff99;
                break;
            case 1: mBackground = 0xffffcccc;
                break;
            case 2: mBackground = 0xffafeeee;
                break;
            default: mBackground = 0xfffafafa;
                break;
        }
    }

    public int getBackground() {
        return mBackground;
    }

    public void setMagnets(List<Box> magnets) {
        mMagnets = magnets;
    }

    public void addMagnet(Box magnet) {
        if (mMagnets == null) {
            mMagnets = new ArrayList<>();
        }
        mMagnets.add(magnet);
    }

    public int getID() {
        return mID;
    }

    public List<Box> getMagnets() {
        return mMagnets;
    }
}
