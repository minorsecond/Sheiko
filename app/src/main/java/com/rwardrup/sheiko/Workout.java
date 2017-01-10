package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/9/17.
 */

public class Workout {
    private int _id;
    private String lift_name;
    private int sets;
    private int reps;
    private Double percentage;
    private int day_number;
    private int cycle_number;
    private int week_number;
    private int exercise_category;

    public Workout() {
    }

    public Workout(String programName, int workoutId) {
        super();
        this._id = _id;
        this.lift_name = lift_name;
        this.sets = sets;
        this.reps = reps;
        this.percentage = percentage;
        this.day_number = day_number;
        this.cycle_number = cycle_number;
        this.week_number = week_number;
        this.exercise_category = exercise_category;
    }
}
