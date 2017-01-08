package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SheikoDb";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_HISTORY_TABLE = "CREATE TABLE `history` (\n" +
                "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`workoutId`\tTEXT NOT NULL UNIQUE,\n" +
                "\t`date`\tINTEGER NOT NULL,\n" +
                "\tFOREIGN KEY(`workoutId`) REFERENCES `workouts`(`workout_uuid`)\n" +
                ");";

        String CREATE_CUSTOM_WORKOUT_TABLE = "CREATE TABLE `customPrograms` (\n" +
                "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`customWorkoutName`\tTEXT NOT NULL,\n" +
                "\t`lift_name`\tTEXT NOT NULL,\n" +
                "\t`sets`\tINTEGER NOT NULL,\n" +
                "\t`reps`\tINTEGER NOT NULL,\n" +
                "\t`percentage`\tREAL NOT NULL,\n" +
                "\t`day_number`\tINTEGER NOT NULL,\n" +
                "\t`cycle_number`\tINTEGER NOT NULL,\n" +
                "\t`week_number`\tINTEGER NOT NULL,\n" +
                "\t`exercise_category`\tINTEGER NOT NULL\n" +
                ");";

        String CREATE_WORKOUT_ADVANCED_MEDIUM_LOAD_TABLE =
                "CREATE TABLE `workout_advanced_medium_load` (\n" +
                        "\t`_id`\tINTEGER NOT NULL UNIQUE,\n" +
                        "\t`lift_name`\tTEXT NOT NULL,\n" +
                        "\t`sets`\tINTEGER NOT NULL,\n" +
                        "\t`reps`\tINTEGER NOT NULL,\n" +
                        "\t`percentage`\tREAL NOT NULL,\n" +
                        "\t`day_number`\tINTEGER NOT NULL,\n" +
                        "\t`cycle_number`\tINTEGER NOT NULL,\n" +
                        "\t`week_number`\tINTEGER NOT NULL,\n" +
                        "\t`exercise_category`\tINTEGER\n" +
                        ");";

        // create tables
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_CUSTOM_WORKOUT_TABLE);
        db.execSQL(CREATE_WORKOUT_ADVANCED_MEDIUM_LOAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS history");

        // create fresh tables
        this.onCreate(db);
    }

}