package com.biz.tizzy.songle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.biz.tizzy.songle.collectedLyrics.Box;
import com.biz.tizzy.songle.collectedLyrics.FridgeScreen;
import com.biz.tizzy.songle.database.LyricBaseHelper;
import com.biz.tizzy.songle.database.LyricCursorWrapper;
import com.biz.tizzy.songle.database.LyricDBSchema;
import com.biz.tizzy.songle.map.MapPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tizzy on 12/4/17.
 */

public class LyricsSingleton {
    private static LyricsSingleton sLyricsSingleton;

    private FridgeScreen[] mScreens;
    private List<Box> mYellowLyrics;
    private List<Box> mRedLyrics;
    private List<Box> mBlueLyrics;
    private MapPoint[] mMapPoints;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static LyricsSingleton get(Context context) {
        if (sLyricsSingleton == null) {
            sLyricsSingleton = new LyricsSingleton(context);
        }
        return sLyricsSingleton;
    }

    private LyricsSingleton(Context context) {
        // open database
        mContext = context.getApplicationContext();
        mDatabase = new LyricBaseHelper(mContext).getWritableDatabase();

        mScreens = new FridgeScreen[3];

        for (int i = 0; i < 3; i++) {
            FridgeScreen screen = new FridgeScreen(i);
            mScreens[i] = screen;
        }
    }

    public void setLyrics(List<Box> yellowLyrics, List<Box> redLyrics, List<Box> blueLyrics) {
        mYellowLyrics = yellowLyrics;
        mRedLyrics = redLyrics;
        mBlueLyrics = blueLyrics;
    }

    public void setMapPoints(MapPoint[] mapPoints) {
        mMapPoints = mapPoints;
    }

    public void addMapPoint(MapPoint mapPoint) {
        ContentValues values = getContentValues(
                mapPoint.getLyricLocation(),
                mapPoint.getLat(),
                mapPoint.getLon(),
                mapPoint.getInterest(),
                null,
                null,
                null,
                null,
                null,
                null,
                "false"
        );

        mDatabase.insert(LyricDBSchema.LyricTable.NAME, null, values);
    }

    //public void addYellowLyric(String lyricLocation, Double lat, Double lon, int interest, String lyric) {
    public void addYellowLyric(String lyric, String lyricLocation) {
        //mYellowLyrics.add(lyric);

        // lyricLocation specifies which row is updated

        Box box = makeBox(lyric);
        mScreens[0].addMagnet(box); // ?

        // values to assign to row
        MapPoint mapPoint = getMapPointWithLyric(lyricLocation);
        ContentValues values = getContentValues( lyricLocation, mapPoint.getLat(), mapPoint.getLon(),
                mapPoint.getInterest(), lyric, box.getTop(), box.getBottom(), box.getLeft(), box.getRight(), "yellow", "true");

        mDatabase.insert(LyricDBSchema.LyricTable.NAME, null, values);
    }
    public void addRedLyric(String lyric, String lyricLocation) {
        //mRedLyrics.add(lyric);
        Box box = makeBox(lyric);
        mScreens[1].addMagnet(box);

        MapPoint mapPoint = getMapPointWithLyric(lyricLocation);
        ContentValues values = getContentValues( lyricLocation, mapPoint.getLat(), mapPoint.getLon(),
                mapPoint.getInterest(), lyric, box.getTop(), box.getBottom(), box.getLeft(), box.getRight(), "red", "true");

        mDatabase.insert(LyricDBSchema.LyricTable.NAME, null, values);

    }
    public void addBlueLyric(String lyric, String lyricLocation) {
        //mBlueLyrics.add(lyric);
        Box box = makeBox(lyric);
        mScreens[2].addMagnet(box);

        MapPoint mapPoint = getMapPointWithLyric(lyricLocation);
        ContentValues values = getContentValues( lyricLocation, mapPoint.getLat(), mapPoint.getLon(),
                mapPoint.getInterest(), lyric, box.getTop(), box.getBottom(), box.getLeft(), box.getRight(), "blue", "true");

        mDatabase.insert(LyricDBSchema.LyricTable.NAME, null, values);

    }

    public void setLyricToFound(String lyricLocation, String lyric) {

        MapPoint mapPoint = getMapPointWithLyric(lyricLocation);
        //String lyric = getLyricAtLocation(lyricLocation);

        // Generate random starting location for box
        Box box = makeBox(lyric);

        // Color
        String color = "yellow";

        ContentValues values = getContentValues(
                mapPoint.getLyricLocation(),
                mapPoint.getLat(),
                mapPoint.getLon(),
                mapPoint.getInterest(),
                lyric,
                box.getTop(),
                box.getBottom(),
                box.getLeft(),
                box.getRight(),
                color,
                "true"
        );

        mDatabase.update(LyricDBSchema.LyricTable.NAME, values,
                LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION + " = ?",
                new String[] { lyricLocation });
    }

    public MapPoint[] getMapPoints()  {
        List<MapPoint> mapPointList = new ArrayList<>();

        // need to check they aren't already found
        // for when returning to map from puzzle view

        LyricCursorWrapper cursor = queryLyrics(
                LyricDBSchema.LyricTable.Cols.FOUND + " = ?",
                new String[] { "false" });

        try {
            // read row by row
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mapPointList.add(cursor.getMapPoint());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        MapPoint[] mapPoints = new MapPoint[mapPointList.size()];
        mapPoints = mapPointList.toArray(mapPoints);
        return mapPoints;
    }

    public MapPoint getMapPointWithLyric(String lyricLoc) {
        LyricCursorWrapper cursor = queryLyrics(
                // does this work ??
                LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION + " = ?",
                new String[] { lyricLoc }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMapPoint();
        } finally {
            cursor.close();
        }
    }

    public MapPoint getMapPointWithLocation(Double lat, Double lon) {

        LyricCursorWrapper cursor = queryLyrics(
                // does this work ??
                LyricDBSchema.LyricTable.Cols.LAT + " and " +
                LyricDBSchema.LyricTable.Cols.LON + " = ?",
                new String[] {lat.toString(), lon.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMapPoint();
        } finally {
            cursor.close();
        }
    }

    public String getLyricAtLocation(String location) {
        LyricCursorWrapper cursor = queryLyrics(
                LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION
                        + " = ?", new String[] { location });
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getLyric();
        } finally {
            cursor.close();
        }
    }

    public List<Box> getYellowBoxes() {
        //return mYellowLyrics;

        List<Box> boxes = new ArrayList<>();

        LyricCursorWrapper cursor = queryLyrics(
                LyricDBSchema.LyricTable.Cols.SCREEN_COLOR + " = ?",
                 new String[] { "yellow" }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                boxes.add(cursor.getBox());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return boxes;
    }
    public List<Box> getRedBoxes() {
        //return mRedLyrics;
        List<Box> boxes = new ArrayList<>();

        LyricCursorWrapper cursor = queryLyrics(
                LyricDBSchema.LyricTable.Cols.SCREEN_COLOR + " = ?",
                new String[] { "red" }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                boxes.add(cursor.getBox());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return boxes;
    }
    public List<Box> getBlueBoxes() {
        //return mBlueLyrics;
        List<Box> boxes = new ArrayList<>();

        LyricCursorWrapper cursor = queryLyrics(
                LyricDBSchema.LyricTable.Cols.SCREEN_COLOR + " = ?",
                new String[] { "blue" }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                boxes.add(cursor.getBox());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return boxes;
    }

    public FridgeScreen[] getScreens() {
        return mScreens;
    }

    public FridgeScreen getScreen(int id) {
        return mScreens[id];
    }

    public Box makeBox(String text) {
        int l = text.length();

        // Generate 2 random numbers
        Random rand = new Random();
        float x = rand.nextInt(800);
        float y = rand.nextInt(1400);

        Box box = new Box(x, y, x + (l*50), y + 100, text);

        return box;
    }

    public void deleteAll() {
        mDatabase.execSQL("delete from " + LyricDBSchema.LyricTable.NAME);
    }

    // Update rows in database
    public void updateLyric(String lyricLocation, Double lat, Double lon,
                            int interest, String lyric, Box box, String color, String found) {

        // can be passed null values ??
        ContentValues values;
        if (box != null) {
            values = getContentValues(
                    lyricLocation, lat, lon, interest, lyric,
                    box.getTop(), box.getBottom(), box.getLeft(), box.getRight(),
                    color, found);
        } else {
            values = getContentValues(
                    lyricLocation, lat, lon, interest, lyric,
                    null, null, null, null,
                    color, found);
        }

        mDatabase.update(LyricDBSchema.LyricTable.NAME, values,
                LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION + " = ?",  // avoid SQL injection
                new String[] { lyricLocation }); // specify which rows get updated

    }

    private static ContentValues getContentValues(
            String lyricLocation, Double lat, Double lon, int interest,
            String lyric, Float top, Float bottom, Float left, Float right, String color, String found) {

        ContentValues values = new ContentValues();
                    // key, value
        values.put(LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION, lyricLocation);
        values.put(LyricDBSchema.LyricTable.Cols.LAT, lat);
        values.put(LyricDBSchema.LyricTable.Cols.LON, lon);
        values.put(LyricDBSchema.LyricTable.Cols.INTEREST, interest);
        values.put(LyricDBSchema.LyricTable.Cols.LYRIC, lyric);

        // box
        values.put(LyricDBSchema.LyricTable.Cols.BOX_TOP, top);
        values.put(LyricDBSchema.LyricTable.Cols.BOX_BOTTOM, bottom);
        values.put(LyricDBSchema.LyricTable.Cols.BOX_LEFT, left);
        values.put(LyricDBSchema.LyricTable.Cols.BOX_RIGHT, right);

        values.put(LyricDBSchema.LyricTable.Cols.SCREEN_COLOR, color);
        //values.put(LyricDBSchema.LyricTable.Cols.FOUND, found ? 1 : 0);
        values.put(LyricDBSchema.LyricTable.Cols.FOUND, found);     // "true" or "false"

        return values;
    }

    private LyricCursorWrapper queryLyrics(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                LyricDBSchema.LyricTable.NAME,
                null,   // select all columns
                whereClause,
                whereArgs,
                null,   // group by
                null,   //having
                null   // order by
        );
        return new LyricCursorWrapper(cursor);
    }


}
