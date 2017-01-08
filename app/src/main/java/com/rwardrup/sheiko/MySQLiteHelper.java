package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Table names
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_CUSTOM_WORKOUTS = "customPrograms";
    private static final String TABLE_ADVANCED_MEDIUM_LOAD = "workout_advanced_medium_load";

    // Table columns
    private static final String ID = "_id";
    private static final String customWorkoutName = "customWorkoutName";
    private static final String workoutId = "workoutId";
    private static final String liftName = "lift_name";
    private static final String sets = "sets";
    private static final String reps = "reps";
    private static final String percentage = "percentage";
    private static final String dayNumber = "day_number";
    private static final String cycleNumber = "cycle_number";
    private static final String weekNumber = "week_number";
    private static final String exerciseCategory = "exercise_category";
    private static final String date = "date";

    // Define columns in each table
    private static final String[] historyColumns = {ID, workoutId, date};

    private static final String[] customProgramColumns = {ID, customWorkoutName, liftName, sets,
            reps, percentage, dayNumber, cycleNumber, weekNumber, exerciseCategory};

    private static final String[] advancedMediumLoadColumns = {ID, liftName, sets, reps, percentage,
            dayNumber, cycleNumber, weekNumber, exerciseCategory};

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

    // Add workout history
    public void addWorkoutHistory(Workout workout) {
        // For logging
        Log.d("addWorkout", workout.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(workoutId, workout.getWorkoutId());
        values.put(date, workout.getDate());

        // 3. Insert
        db.insert(TABLE_HISTORY,  // table
                null,  // nullColumnHack
                values); // key/value keys = column names/ values = col

        // 4. Close
        db.close();
    }

    // Get workout history by date
    public Workout getSpecificWorkoutByDate(int date) {

        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build query TODO: make this actually do what I want (get by date integer)
        Cursor cursor = db.query(TABLE_HISTORY,
                historyColumns,
                " date = ?", // selections
                new String[]{String.valueOf(date)},  // Selection's args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // 3. if we got results, show the first
        if (cursor != null)
            cursor.moveToFirst();

        // 4. Build workout object
        Workout workout = new Workout();
        workout.setWorkoutId(cursor.getString(0));
        workout.setWorkoutName(cursor.getString(1));
        workout.setDate(cursor.getInt(2));

        // Log
        Log.d("getWorkout(" + date + ")", workout.toString());

        // 5. Close cursor
        cursor.close();

        // 6. Return workout
        return workout;
    }

    // Get all workout history
    public List<Workout> getAllWorkoutHistory() {
        List<Workout> workouts = new LinkedList<Workout>();

        // 1. Build the query
        String query = "SELECT * FROM " + TABLE_HISTORY;

        // 2. Get reference to the writable db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. Go over each row. Build workout and add it to list
        Workout workout = null;
        if (cursor.moveToFirst()) {
            do {
                workout = new Workout();
                workout.setWorkoutId(cursor.getString(0));
                workout.setWorkoutName(cursor.getString(1));
                workout.setDate(cursor.getInt(2));

                // Add workout to workouts
                workouts.add(workout);
            } while (cursor.moveToNext());
        }

        Log.d("getAllWorkouts()", workouts.toString());

        return workouts;
    }

}