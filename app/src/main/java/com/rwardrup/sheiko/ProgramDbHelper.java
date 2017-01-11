package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class ProgramDbHelper extends SQLiteOpenHelper {

    // Table name
    private static final String TABLE_PROGRAMS = "programs";

    // Table columns
    private static final String ID = "_id";
    private static final String programTableName = "progranName";
    private static final String dayExerciseNumber = "dayExerciseNumber";
    private static final String exerciseName = "exerciseName";
    private static final String workoutId = "workoutId";
    private static final String reps = "reps";
    private static final String dayNumber = "dayNuumber";
    private static final String cycleNumber = "cycleNumber";
    private static final String weekNumber = "weekNumber";
    private static final String exerciseCategory = "exerciseCategory";
    private static final String weightPercentage = "weightPercentage";
    private static final String enabled = "enabled";


    // Define columns in each table
    private static final String[] programColumns = {ID, workoutId, programTableName, cycleNumber,
            weekNumber, dayNumber, exerciseName, exerciseCategory, dayExerciseNumber, reps,
            weightPercentage, enabled};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "workoutPrograms";

    public ProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<WorkoutProgram> getTodaysWorkout(String programName, int cycleNumber, int weekNumber, int dayNumber) {

        List<WorkoutProgram> todaysWorkout = new LinkedList<>();
        // 1. Get reference to readable db
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "programName = " + "'" + programName + "'" + " AND cycleNumber = " + cycleNumber +
                " AND weekNumber = " + weekNumber + " AND dayNumber = " + dayNumber;

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
        WorkoutProgram _todaysWorkout = null;
        if (cursor.moveToFirst()) {
            do {
                _todaysWorkout = new WorkoutProgram();
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
}