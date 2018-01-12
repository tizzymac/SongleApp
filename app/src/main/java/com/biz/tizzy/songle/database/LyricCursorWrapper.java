package com.biz.tizzy.songle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.biz.tizzy.songle.collectedLyrics.Box;
import com.biz.tizzy.songle.map.MapPoint;

/**
 * Created by tizzy on 12/6/17.
 */

public class LyricCursorWrapper extends CursorWrapper {

    public LyricCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public MapPoint getMapPoint() {
        String lyricLocation = getString(getColumnIndex(LyricDBSchema.LyricTable.Cols.LYRIC_LOCATION));
        Double lat = getDouble(getColumnIndex(LyricDBSchema.LyricTable.Cols.LAT));
        Double lon = getDouble(getColumnIndex(LyricDBSchema.LyricTable.Cols.LON));
        int interest = getInt(getColumnIndex(LyricDBSchema.LyricTable.Cols.INTEREST));

        MapPoint mapPoint = new MapPoint(lat,lon);
        mapPoint.setLyricLocation(lyricLocation);
        mapPoint.setInterest(interest);

        return mapPoint;
    }

    public Box getBox() {
        String lyric = getString(getColumnIndex(LyricDBSchema.LyricTable.Cols.LYRIC));
        Float top = getFloat(getColumnIndex(LyricDBSchema.LyricTable.Cols.BOX_TOP));
        Float bottom = getFloat(getColumnIndex(LyricDBSchema.LyricTable.Cols.BOX_BOTTOM));
        Float left = getFloat(getColumnIndex(LyricDBSchema.LyricTable.Cols.BOX_LEFT));
        Float right = getFloat(getColumnIndex(LyricDBSchema.LyricTable.Cols.BOX_RIGHT));

        Box box = new Box(left, top, right, bottom, lyric);

        return box;
    }

    public String getLyric() {
        String lyric = getString(getColumnIndex(LyricDBSchema.LyricTable.Cols.LYRIC));
        return lyric;
    }
}
