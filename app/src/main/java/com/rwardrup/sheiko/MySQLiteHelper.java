package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteAssetHelper {

    // Table names
    private static final String TABLE_HISTORY = "workoutHistory";
    private static final String TABLE_STATS = "workoutStats";
    private static final String TABLE_USER_MAXES = "userMaxes";
    private static final String TABLE_CUSTOM_WORKOUTS = "customPrograms";
    private static final String TABLE_PROGRAMS = "programs";

    // Table columns
    private static final String ID = "_id";
    private static final String dayExerciseNumber = "dayExerciseNumber";
    private static final String customWorkoutName = "customWorkoutName";
    private static final String exercise = "exercise";
    private static final String weight = "weight";
    private static final String programTableName = "programTableName";
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
    private static final String weightPercentage = "weightPercentage";
    private static final String enabled = "enabled";

    // Define columns in each table
    private static final String[] statsColumns = {ID, workoutId, date};

    private static final String[] workoutHistoryColumns = {ID, workoutId, date, exercise, reps,
            weight, programTableName};

    private static final String[] customProgramColumns = {ID, customWorkoutName, liftName, sets,
            reps, percentage, dayNumber, cycleNumber, weekNumber, exerciseCategory};

    private static final String[] programColumns = {ID, workoutId, programTableName, cycleNumber,
            weekNumber, dayNumber, exercise, exerciseCategory, dayExerciseNumber, reps, weightPercentage,
            enabled};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SheikoDbTest";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS workoutStats");
        db.execSQL("DROP TABLE IF EXISTS workoutHistory");

        // create fresh tables
        this.onCreate(db);
    }

    public List<WorkoutSet> getTodaysWorkout(String programName, int cycleNumber, int weekNumber, int dayNumber) {

        List<WorkoutSet> todaysWorkout = new LinkedList<>();
        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "programTableName = " + "'" + programName + "'" + " AND cycle_number = "
                + cycleNumber + " AND week_number = " + weekNumber + " AND day_number = " +
                dayNumber;

        Log.i("TodaysWorkout", "Program DB query: " + query);

        // 2. Build query TODO: make this actually do what I want (get by date integer)
        Cursor cursor = db.query(TABLE_PROGRAMS,
                programColumns,
                query, // selections
                null,  // Selection's args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // 3. if we got results, show the first
        if (cursor != null)
            cursor.moveToFirst();

        // 3. Go over each row. Build workout and add it to list
        WorkoutSet _todaysWorkout = null;
        if (cursor.moveToFirst()) {
            do {
                _todaysWorkout = new WorkoutSet();
                _todaysWorkout.setWorkoutId(cursor.getString(1));
                _todaysWorkout.setProgramName(cursor.getString(2));
                _todaysWorkout.setCycleNumber(cursor.getInt(3));
                _todaysWorkout.setWeekNumber(cursor.getInt(4));
                _todaysWorkout.setDayNumber(cursor.getInt(5));
                _todaysWorkout.setExerciseName(cursor.getString(6));
                _todaysWorkout.setExerciseCategory(cursor.getInt(7));
                _todaysWorkout.setDayExerciseNumber(cursor.getInt(8));
                _todaysWorkout.setReps(cursor.getInt(9));
                _todaysWorkout.setWeightPercentage(cursor.getDouble(10));
                _todaysWorkout.setEnabled(cursor.getInt(11));

                // Add workout to workouts
                todaysWorkout.add(_todaysWorkout);
            } while (cursor.moveToNext());
        }

        Log.d("getTodaysWorkout()", todaysWorkout.toString());

        // Close the cursor
        cursor.close();

        return todaysWorkout;

    }

    public int getWorkoutHistoryRowCount() {
        String countQuery = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        return cnt;
    }

    public void addWorkoutHistory(WorkoutHistory workoutHistory) {
        // For logging
        Log.d("addWorkout", workoutHistory.toString());

        // 1. Get writable Db
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(workoutId, workoutHistory.getWorkoutId());
        values.put(date, workoutHistory.getDate());
        values.put(exercise, workoutHistory.getExercise());
        values.put(reps, workoutHistory.getReps());
        values.put(weight, workoutHistory.getWeight());
        values.put(programTableName, workoutHistory.getProgramTableName());

        db.insert(TABLE_HISTORY,
                null,
                values);

        db.close();

    }

    public WorkoutHistory getWorkoutHistoryAtId(int id) {
        Log.i("WorkoutHistory", "Getting workoutId: " + id);

        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build query
        Cursor cursor = db.query(TABLE_HISTORY,
                workoutHistoryColumns,
                " workoutId = ?", // selections
                new String[]{String.valueOf(id)},  // Selection's args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // 3. if we got results, show the first
        if (cursor != null)
            cursor.moveToFirst();

        // 4. Build workout object
        WorkoutHistory workoutHistory = new WorkoutHistory();
        workoutHistory.setWorkoutId(cursor.getString(1));
        workoutHistory.setDate(cursor.getString(2));
        workoutHistory.setExercise(cursor.getString(3));
        workoutHistory.setReps(cursor.getInt(4));
        workoutHistory.setWeight(cursor.getDouble(5));
        workoutHistory.setProgramTableName(cursor.getString(6));

        // Log
        Log.d("getWorkout(" + workoutId + ")", workoutHistory.toString());

        // 5. Close cursor
        cursor.close();

        // 6. Return workout
        return workoutHistory;
    }

    // Get all workout STATS
    public List<WorkoutHistory> getAllWorkoutHistory() {
        List<WorkoutHistory> allWorkoutHistory = new LinkedList<WorkoutHistory>();

        // 1. Build the query
        String query = "SELECT * FROM " + TABLE_HISTORY;

        // 2. Get reference to the writable db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. Go over each row. Build workout and add it to list
        WorkoutHistory workoutHistory = null;
        if (cursor.moveToFirst()) {
            do {
                workoutHistory = new WorkoutHistory();
                workoutHistory.setWorkoutId(cursor.getString(1));
                workoutHistory.setDate(cursor.getString(2));
                workoutHistory.setExercise(cursor.getString(3));
                workoutHistory.setSets(cursor.getInt(4));
                workoutHistory.setWeight(cursor.getDouble(5));
                workoutHistory.setProgramTableName(cursor.getString(6));


                // Add workout to workouts
                allWorkoutHistory.add(workoutHistory);
            } while (cursor.moveToNext());
        }

        Log.d("getAllWorkouts()", allWorkoutHistory.toString());

        // Close the cursor
        cursor.close();

        return allWorkoutHistory;
    }

    public int changeWorkoutHistoryAtId(int id, WorkoutHistory workoutHistory) {
        Log.i("WorkoutHistory", "Changing workout history row " + id);
        Log.d("WorkoutHistoryId", workoutHistory.getWorkoutId());

        // 1. Get writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues
        ContentValues values = new ContentValues();
        //values.put(workoutId = workoutHistory.getWorkoutId());
        values.put(date, workoutHistory.getDate());
        values.put(exercise, workoutHistory.getExercise());
        values.put(reps, workoutHistory.getReps());
        values.put(weight, workoutHistory.getWeight());
        values.put(programTableName, workoutHistory.getProgramTableName());

        // 3. Update row
        int i = db.update(TABLE_HISTORY,
                values,
                "_id=" + id,
                null);  // TODO: Map this to pkey

        // 4. Close DB
        db.close();

        return i;

    }

    // Add workout stats
    public void addWorkoutStats(WorkoutStats workoutStatistics) {
        // For logging
        Log.d("addWorkout", workoutStatistics.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(workoutId, workoutStatistics.getWorkoutId());
        values.put(date, workoutStatistics.getDate());

        // 3. Insert
        db.insert(TABLE_STATS,  // table
                null,  // nullColumnHack
                values); // key/value keys = column names/ values = col

        // 4. Close
        db.close();
    }

    // Get workout stats by date
    public WorkoutStats getSpecificWorkoutByDate(int date) {

        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build query TODO: make this actually do what I want (get by date integer)
        Cursor cursor = db.query(TABLE_STATS,
                statsColumns,
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
        WorkoutStats workoutStatistics = new WorkoutStats();
        workoutStatistics.setWorkoutId(cursor.getString(1));
        workoutStatistics.setDate(cursor.getString(2));

        // Log
        Log.d("getWorkout(" + date + ")", workoutStatistics.toString());

        // 5. Close cursor
        cursor.close();

        // 6. Return workout
        return workoutStatistics;
    }

    // Get all workout STATS
    public List<WorkoutStats> getAllWorkoutstats() {
        List<WorkoutStats> workoutStatisticses = new LinkedList<WorkoutStats>();

        // 1. Build the query
        String query = "SELECT * FROM " + TABLE_STATS;

        // 2. Get reference to the writable db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. Go over each row. Build workout and add it to list
        WorkoutStats workoutStatistics = null;
        if (cursor.moveToFirst()) {
            do {
                workoutStatistics = new WorkoutStats();
                workoutStatistics.setWorkoutId(cursor.getString(1));
                workoutStatistics.setDate(cursor.getString(2));
                workoutStatistics.setSquatSets(cursor.getInt(3));
                workoutStatistics.setBenchSets(cursor.getInt(4));
                workoutStatistics.setDeadliftSets(cursor.getInt(5));
                workoutStatistics.setSquatTotalWeight(cursor.getInt(6));
                workoutStatistics.setBenchTotalWeight(cursor.getInt(7));
                workoutStatistics.setDeadliftTotalWeight(cursor.getInt(8));
                workoutStatistics.setSquatReps(cursor.getInt(9));
                workoutStatistics.setBenchReps(cursor.getInt(10));
                workoutStatistics.setDeadliftReps(cursor.getInt(11));
                workoutStatistics.setAverageWeightLiftedAll();

                // Add workout to workouts
                workoutStatisticses.add(workoutStatistics);
            } while (cursor.moveToNext());
        }

        Log.d("getAllWorkouts()", workoutStatisticses.toString());

        // Close the cursor
        cursor.close();

        return workoutStatisticses;
    }

    // Update workout statistics
    public int updateWorkoutStats(WorkoutStats workoutStatistics) {
        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(workoutId, workoutStatistics.getWorkoutId());
        values.put(date, workoutStatistics.getDate());

        // 3. Update row
        int i = db.update(TABLE_STATS,
                values,
                ID + " = ?",
                new String[]{String.valueOf(workoutStatistics.getWorkoutId())});

        // 5. Close DB
        db.close();

        return i;
    }

    // Delete workout
    public void deleteWorkoutStats(WorkoutStats workoutStatistics) {
        // 1. Get reference to writable db
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Delete
        db.delete(TABLE_STATS, ID + " = ?", new String[]{String.valueOf(workoutStatistics.getWorkoutId())});

        //3. Close
        db.close();

        // log
        Log.d("DeleteWorkout", workoutStatistics.toString());
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