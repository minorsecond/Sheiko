package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 * This is the bean for workouts to be store in Sqlite history database
 */

public class Workout {

    private int _id; // PKEY
    private String workoutId;  // Identifies the last workout performed
    private int date;  // date the workout was performed

    public Workout() {
    }

    public Workout(String workoutId, int date) {
        super();
        this.workoutId = workoutId;
        this.date = date;
    }

    // Getters & Setters
    @Override
    public String toString() {
        return "Workout [_id=" + _id + ", workoutId=" + workoutId + ", date=" + date
                + "]";
    }
}