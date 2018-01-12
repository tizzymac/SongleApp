package com.biz.tizzy.songle.collectedLyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tizzy on 10/16/17.
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mTextPaint;

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x600BB5FF);

        // Paint the text white
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(60f);

        try {
            mBoxen = ((SetScreenLyricsListener) context).setScreenLyrics();
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SetScreenLyricsListener");
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                //mCurrentBox.checkClickIsInBox(current);
                mCurrentBox = whichBoxWasClicked(current);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";

                if (mCurrentBox != null) {
                    mCurrentBox.updatePosition(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                if (mCurrentBox != null) {
                    mCurrentBox.dropped();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                if (mCurrentBox != null) {
                    mCurrentBox.dropped();
                }
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBoxen != null) {
            for (Box box : mBoxen) {
                float left = box.getLeft();
                float right = box.getRight();
                float top = box.getTop();
                float bottom = box.getBottom();

                float textX = left + 20;
                float textY = 20 + (top + bottom) / 2;

                canvas.drawRect(left, top, right, bottom, mBoxPaint);
                canvas.drawText(box.getText(), textX, textY, mTextPaint);
            }
        }
    }

    public Box whichBoxWasClicked(PointF current) {

        if (mBoxen != null) {
            for (Box box : mBoxen) {
                box.checkClickIsInBox(current);
                if (box.isClicked()) {
                    return box;
                }
            }
        }
        return null;
    }
}