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
    private static final String TABLE_USER_MAXES = "userMaxes";
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
    private static final String units = "units";
    private static final String squatMax = "squat_max";
    private static final String benchMax = "bench_max";
    private static final String deadliftMax = "deadlift_max";
    private static final String wilks = "wilks";



    // Define columns in each table
    private static final String[] historyColumns = {ID, workoutId, date};

    private static final String[] customProgramColumns = {ID, customWorkoutName, liftName, sets,
            reps, percentage, dayNumber, cycleNumber, weekNumber, exerciseCategory};

    private static final String[] advancedMediumLoadColumns = {ID, liftName, sets, reps, percentage,
            dayNumber, cycleNumber, weekNumber, exerciseCategory};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SheikoDbTest";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_HISTORY_TABLE = "CREATE TABLE `history` (\n" +
                "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`workoutId`\tTEXT NOT NULL UNIQUE,\n" +
                "\t`date`\tTEXT NOT NULL,\n" +
                "\t`squatSets`\tINTEGER,\n" +
                "\t`benchSets`\tINTEGER,\n" +
                "\t`deadliftSets`\tINTEGER,\n" +
                "\t`squatTotalWeight`\tINTEGER,\n" +
                "\t`benchTotalWeight`\tINTEGER,\n" +
                "\t`deadliftTotalWeight`\tINTEGER,\n" +
                "\t`squatReps`\tINTEGER,\n" +
                "\t`benchReps`\tINTEGER,\n" +
                "\t`deadliftReps`\tINTEGER,\n" +
                "\t`programTableName`\tTEXT\n" +
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
                        "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "\t`lift_name`\tTEXT NOT NULL,\n" +
                        "\t`sets`\tINTEGER NOT NULL,\n" +
                        "\t`reps`\tINTEGER NOT NULL,\n" +
                        "\t`percentage`\tREAL NOT NULL,\n" +
                        "\t`day_number`\tINTEGER NOT NULL,\n" +
                        "\t`cycle_number`\tINTEGER NOT NULL,\n" +
                        "\t`week_number`\tINTEGER NOT NULL,\n" +
                        "\t`exercise_category`\tINTEGER\n" +
                        ");";

        String CREATE_USER_MAX_TABLE =
                "CREATE TABLE `userMaxes` (\n" +
                        "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                        "\t`units`\tTEXT,\n" +
                        "\t`squat_max`\tREAL,\n" +
                        "\t`bench_max`\tREAL,\n" +
                        "\t`deadlift_max`\tREAL,\n" +
                        "\t`date`\tTEXT,\n" +
                        "\t`wilks`\tREAL\n" +
                        ");";

        // create tables
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_USER_MAX_TABLE);
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
        workout.setWorkoutId(cursor.getString(1));
        workout.setDate(cursor.getString(2));

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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. Go over each row. Build workout and add it to list
        Workout workout = null;
        if (cursor.moveToFirst()) {
            do {
                workout = new Workout();
                workout.setWorkoutId(cursor.getString(1));
                workout.setDate(cursor.getString(2));
                workout.setSquatSets(cursor.getInt(3));
                workout.setBenchSets(cursor.getInt(4));
                workout.setDeadliftSets(cursor.getInt(5));
                workout.setSquatTotalWeight(cursor.getInt(6));
                workout.setBenchTotalWeight(cursor.getInt(7));
                workout.setDeadliftTotalWeight(cursor.getInt(8));
                workout.setSquatReps(cursor.getInt(9));
                workout.setBenchReps(cursor.getInt(10));
                workout.setDeadliftReps(cursor.getInt(11));
                workout.setAverageWeightLiftedAll();

                // Add workout to workouts
                workouts.add(workout);
            } while (cursor.moveToNext());
        }

        Log.d("getAllWorkouts()", workouts.toString());

        // Close the cursor
        cursor.close();

        return workouts;
    }

    // Update workout history
    public int updateWorkoutHistory(Workout workout) {
        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(workoutId, workout.getWorkoutId());
        values.put(date, workout.getDate());

        // 3. Update row
        int i = db.update(TABLE_HISTORY,
                values,
                ID + " = ?",
                new String[]{String.valueOf(workout.getWorkoutId())});

        // 5. Close DB
        db.close();

        return i;
    }

    // Delete workout
    public void deleteWorkoutFromHistory(Workout workout) {
        // 1. Get reference to writable db
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Delete
        db.delete(TABLE_HISTORY, ID + " = ?", new String[]{String.valueOf(workout.getWorkoutId())});

        //3. Close
        db.close();

        // log
        Log.d("DeleteWorkout", workout.toString());
    }

    // Add user max
    public void addUserMaxEntry(UserMaxEntry userMaxEntry) {
        // For logging
        Log.d("addUserMaxEntry()", userMaxEntry.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(units, userMaxEntry.getUnits());
        values.put(squatMax, userMaxEntry.getSquatMax());
        values.put(benchMax, userMaxEntry.getBenchMax());
        values.put(deadliftMax, userMaxEntry.getDeadliftMax());
        values.put(date, userMaxEntry.getDate());
        values.put(wilks, userMaxEntry.getWilks());

        // 3. Insert
        db.insert(TABLE_USER_MAXES,  // table
                null,  // nullColumnHack
                values); // key/value keys = column names/ values = col

        // 4. Close
        db.close();
    }

    public UserMaxEntry getLastUserMaxEntry() {
        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build query TODO: make this actually do what I want (get by date integer)
        String query = "SELECT * FROM " + TABLE_USER_MAXES;
        Cursor cursor = db.rawQuery(query, null);

        // 3. Make the query
        UserMaxEntry userMaxEntry = null;
        if (cursor.moveToLast()) {
            userMaxEntry = new UserMaxEntry();
            userMaxEntry.setUnits(cursor.getString(1));
            userMaxEntry.setSquatMax(cursor.getDouble(2));
            userMaxEntry.setBenchMax(cursor.getDouble(3));
            userMaxEntry.setDeadliftMax(cursor.getDouble(4));
            userMaxEntry.setWilks(cursor.getDouble(5));
            userMaxEntry.setDate(cursor.getString(6));
            Log.d("getLastUserMaxEntry()", userMaxEntry.toString());
        }
        cursor.close();
        return userMaxEntry;
    }
}