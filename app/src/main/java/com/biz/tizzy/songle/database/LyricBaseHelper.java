package com.biz.tizzy.songle.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biz.tizzy.songle.database.LyricDBSchema.LyricTable;

/**
 * Created by tizzy on 12/6/17.
 */

public class LyricBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "lyricBase.db";

    public LyricBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LyricTable.NAME + "(" +
        // " _id String primary key, " +
        LyricTable.Cols.LYRIC_LOCATION + "," +
        LyricTable.Cols.LYRIC + "," +
        LyricTable.Cols.LAT + "," +
        LyricTable.Cols.LON + "," +
        LyricTable.Cols.INTEREST + "," +
        LyricTable.Cols.BOX_TOP + "," +
        LyricTable.Cols.BOX_BOTTOM + "," +
        LyricTable.Cols.BOX_LEFT + "," +
        LyricTable.Cols.BOX_RIGHT + "," +
        LyricTable.Cols.SCREEN_COLOR + "," +
        LyricTable.Cols.FOUND + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
