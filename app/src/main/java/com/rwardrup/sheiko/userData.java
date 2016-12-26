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
        public static final String USER_UNITS = "units";
        public static final String BODY_WEIGHT = "body_weight";
        public static final String SEX = "sex";

        // Create the sqlite db if it doesn't yet exist.
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_UNITS + " TEXT, " +
                BODY_WEIGHT + " TEXT, " +
                SEX + " TEXT" + ") ";
    }

    public static class UserMaxes implements BaseColumns {
        public static final String TABLE_NAME = "userMaxes";
        public static final String USER_UNITS = "units";
        public static final String SQUAT_MAX = "squat_max";
        public static final String BENCH_MAX = "bench_max";
        public static final String DEADLIFT_MAX = "deadlift_max";
        public static final String DATE = "date";
        public static final String WILKS = "wilks";

        // Create the sqlite db if it doesn't yet exist.
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_UNITS + " TEXT, " +
                SQUAT_MAX + " INTEGER, " +
                BENCH_MAX + " INTEGER, " +
                DEADLIFT_MAX + "INTEGER, " +
                DATE + "INTEGER, " +  // Date will be stored as a long, for sorting
                WILKS + " INTEGER" + ") ";
    }

    public static class WorkoutHistory implements BaseColumns {
        public static final String TABLE_NAME = "workout_history";
        public static final String WORKOUT_NUMBER = "workout_number";
        public static final String WORKOUT_DATE = "workout_date";
        public static final String SQUAT_TOTAL_WEIGHT = "squat_total_weight";
        public static final String SQUAT_TOTAL_VOLUME = "squat_total_volume";
        public static final String BENCH_TOTAL_WEIGHT = "bench_total_weight";
        public static final String BENCH_TOTAL_VOLUME = "bench_total_volume";
        public static final String DEADLIFT_TOTAL_WEIGHT = "deadlift_total_weight";
        public static final String DEADLIFT_TOTAL_VOLUME = "deadlift_total_volume";

        // Create the sqlite db if it doesn't yet exist.
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WORKOUT_NUMBER + " INTEGER, " +
                WORKOUT_DATE + " INTEGER, " +
                SQUAT_TOTAL_WEIGHT + " INTEGER, " +
                SQUAT_TOTAL_VOLUME + " INTEGER, " +
                BENCH_TOTAL_WEIGHT + " INTEGER, " +
                BENCH_TOTAL_VOLUME + " INTEGER, " +
                DEADLIFT_TOTAL_WEIGHT + " INTEGER, " +
                DEADLIFT_TOTAL_VOLUME + "INTEGER" + ") ";
    }

    public static class SquatSessions implements BaseColumns {
        public static final String TABLE_NAME = "SquatSessions";
        public static final String USER_UNITS = "units";
        public static final String SQUAT_SETS = "squat_sets";
        public static final String SQUAT_REPS = "squat_reps";
        public static final String SQUAT_WEIGHT = "squat_weight";

        // Create the sqlite db if it doesn't yet exist.
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_UNITS + " TEXT, " +
                SQUAT_SETS + " INTEGER, " +
                SQUAT_REPS + " INTEGER, " +
                SQUAT_WEIGHT + "INTEGER" + ") ";
    }
}
