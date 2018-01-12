package com.biz.tizzy.songle.collectedLyrics;

import android.graphics.PointF;

/**
 * Created by tizzy on 10/16/17.
 */

public class Box {
    private float mLeft;        // changed to float from PointF
    private float mTop;
    private float mRight;
    private float mBottom;
    private String mText;
    private boolean mClicked;

    public Box(float left, float top, float right, float bottom, String text) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        mClicked = false;
        mText = text;
    }

    public float getLeft() {
        return mLeft;
    }
    public float getTop() {
        return mTop;
    }
    public float getRight() {
        return mRight;
    }
    public float getBottom() {
        return mBottom;
    }

    public void updatePosition(PointF current) {
        int l = mText.length();

        mLeft = (current.x - (l*25));
        mRight = (current.x + (l*25));
        mTop = (current.y - 50);
        mBottom = (current.y + 50);
    }

    public void checkClickIsInBox(PointF click) {
        boolean left = (click.x >= mLeft);
        boolean right = (click.x <= mRight);
        boolean top = (click.y >= mTop);
        boolean bottom = (click.y <= mBottom);

        mClicked = ((left & right) & (top & bottom));
        //mClicked = true;
    }

    public boolean isClicked() {
        return mClicked;
    }

    public void dropped() {
        mClicked = false;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
