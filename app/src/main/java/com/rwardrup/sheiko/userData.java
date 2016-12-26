package com.rwardrup.sheiko;

import android.provider.BaseColumns;

/**
 * Created by rwardrup on 12/26/16.
 */

public final class userData {
    private userData() {
    }

    public static class UserParameters implements BaseColumns {
        public static final String TABLE_NAME = "userParameters";
        public static final String COLUMN_NAME = "units";
        public static final String BODY_WEIGHT = "body_weight";
        public static final String SEX = "sex";

        // Create the sqlite db if it doesn't yet exist.
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                BODY_WEIGHT + " TEXT, " +
                SEX + " INTEGER" + ") ";
    }
}
