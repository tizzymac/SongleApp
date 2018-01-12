package com.biz.tizzy.songle.database;

/**
 * Created by tizzy on 12/6/17.
 */

public class LyricDBSchema {

    // LyricTable exists to define the String constants
    // needed to describe the moving pieces of the table definition
    public static final class LyricTable {
        // First piece of that definition
        public static final String NAME = "lyrics";

        // columns
        public static final class Cols {
            public static final String LYRIC_LOCATION = "lyric_location";   // primary key
            public static final String LAT = "lat";
            public static final String LON = "lon";
            public static final String INTEREST = "interest";
            public static final String LYRIC = "lyric";
            public static final String BOX_TOP = "box_top";
            public static final String BOX_BOTTOM = "box_bottom";
            public static final String BOX_RIGHT = "box_right";
            public static final String BOX_LEFT = "box_left";
            public static final String SCREEN_COLOR = "screen_color";
            public static final String FOUND = "found";
        }
    }
}
