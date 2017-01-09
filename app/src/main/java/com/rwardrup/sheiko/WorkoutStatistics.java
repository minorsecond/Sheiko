package com.rwardrup.sheiko;

import android.util.Log;

/**
 * Created by rwardrup on 1/8/17.
 * This is the bean for workouts to be store in Sqlite history database
 */

public class WorkoutStatistics {

    public int totalReps;
    private int _id; // PKEY
    private String workoutId;  // Identifies the last workout performed will be a UUID
    private String date;  // date the workout was performed
    private int squatSets;  // date the workout was performed
    private int benchSets;  // date the workout was performed
    private int deadliftSets;  // date the workout was performed
    private int squatTotalWeight;  // date the workout was performed
    private int benchTotalWeight;  // date the workout was performed
    private int deadliftTotalWeight;  // date the workout was performed
    private float averageWeightLiftedAll;
    private float averageWeightLiftedSquat;
    private float averageWeightLiftedBench;
    private float averageWeightLiftedDeadlift;
    private int squatReps;
    private int benchReps;
    private int deadliftReps;

    public WorkoutStatistics() {
    }

    public WorkoutStatistics(String workoutId, String date) {
        super();
        this.workoutId = workoutId;
        this.date = date;
        this.squatSets = squatSets;
        this.benchSets = benchSets;
        this.deadliftSets = deadliftSets;
        this.squatTotalWeight = squatTotalWeight;
        this.benchTotalWeight = benchTotalWeight;
        this.deadliftTotalWeight = deadliftTotalWeight;
        this.squatReps = squatReps;
        this.benchReps = benchReps;
        this.deadliftReps = deadliftReps;
    }

    // Getters
    @Override
    public String toString() {
        return "Workout [_id=" + _id + ", workoutId=" + workoutId + ", date=" + date + ", squatSets="
                + squatSets + ", benchSets=" + benchSets + ", deadliftSets=" + deadliftSets +
                ", squatTotalWeight=" + squatTotalWeight + ", benchTotalWeight=" + benchTotalWeight +
                ", deadliftTotalWeight=" + deadliftTotalWeight + "]";
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotalReps() {
        return this.squatReps + this.benchReps + this.deadliftReps;  // TODO: Make this actually reflect volume
    }

    // Setters

    public float getAverageWeightLiftedAll() {
        return averageWeightLiftedAll;
    }

    public void setSquatSets(int squatSets) {
        this.squatSets = squatSets;
    }

    public void setBenchSets(int benchSets) {
        this.benchSets = benchSets;
    }

    public void setDeadliftSets(int deadliftSets) {
        this.deadliftSets = deadliftSets;
    }

    public void setSquatTotalWeight(int squatTotalWeight) {
        this.squatTotalWeight = squatTotalWeight;
    }

    public void setBenchTotalWeight(int benchTotalWeight) {
        this.benchTotalWeight = benchTotalWeight;
    }

    public void setDeadliftTotalWeight(int deadliftTotalWeight) {
        this.deadliftTotalWeight = deadliftTotalWeight;
    }

    public void setAverageWeightLiftedAll() {
        this.averageWeightLiftedAll = (squatTotalWeight + benchTotalWeight +
                deadliftTotalWeight) / (squatSets + benchSets + deadliftSets);

        Log.d("AverageWeightLiftedAll", "AWL ALL: " + this.averageWeightLiftedAll);
    }

    public void setSquatReps(int squatReps) {
        this.squatReps = squatReps;
        Log.i("SquatReps", "Squat reps: " + this.squatReps);
    }

    public void setBenchReps(int benchReps) {
        this.benchReps = benchReps;
    }

    public void setDeadliftReps(int deadliftReps) {
        this.deadliftReps = deadliftReps;
    }

    public void setTotalReps() {
        this.totalReps = squatReps + benchReps + deadliftReps;
        Log.i("TotalReps", "Total reps: " + this.totalReps);
    }
}
