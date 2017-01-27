package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 * This is the bean for workouts to be store in Sqlite history database
 */

public class WorkoutHistory {

    private int _id; // PKEY
    private String workoutId;  // Identifies the last workout performed will be a UUID
    private String date;  // date the workout was performed
    private String exercise;  // Exercise name
    private int sets;
    private int reps;  // Number reps performed
    private Double weight; // Weight lifted
    private String programTableName;  // Name of table corresponding to program
    private int exerciseNumber;  // Name of table corresponding to program
    private int persist;

    public WorkoutHistory() {
    }

    public WorkoutHistory(String workoutId, String date, String exercise, int reps,
                          Double weight, String programTableName, int exerciseNumber, int persist) {
        super();
        this.workoutId = workoutId;
        this.date = date;
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.programTableName = programTableName;
        this.exerciseNumber = exerciseNumber;
        this.persist = persist;
    }

    // Getters
    @Override
    public String toString() {
        return "Workout [_id=" + _id + ", workoutId=" + workoutId + ", date=" + date + ", " +
                "exercise=" + exercise + ", reps=" + reps + ", weight=" +
                weight + ", programTableName=" + programTableName +
                ", exerciseCategory=" + exerciseNumber + "]";
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

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getProgramTableName() {
        return programTableName;
    }

    public void setProgramTableName(String programTableName) {
        this.programTableName = programTableName;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    public int getPersist() {
        return persist;
    }

    public void setPersist(int persist) {
        this.persist = persist;
    }
}
