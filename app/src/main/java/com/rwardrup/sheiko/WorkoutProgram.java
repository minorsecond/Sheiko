package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/11/17.
 */

public class WorkoutProgram {
    private int _id;
    private String workoutId;
    private String programName;
    private int cycleNumber;
    private int weekNumber;
    private int dayNumber;
    private String exerciseName;
    private int exerciseCategory;
    private int dayExerciseNumber;
    private int reps;
    private Double weightPercentage;
    private int enabled;

    public WorkoutProgram() {
    }

    public WorkoutProgram(String workoutId, String programName, int cycleNumber, int weekNumber,
                          int dayNumber, String exerciseName, int exerciseCategory,
                          int dayExerciseNumber, int reps, Double weightPercentage, int enabled) {
        super();
        this.workoutId = workoutId;
        this.programName = programName;
        this.cycleNumber = cycleNumber;
        this.weekNumber = weekNumber;
        this.dayNumber = dayNumber;
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
        this.dayExerciseNumber = dayExerciseNumber;
        this.reps = reps;
        this.weightPercentage = weightPercentage;
        this.enabled = enabled;

    }

    // Getters
    @Override
    public String toString() {
        return "Workout [_id=" + _id + ", workoutId=" + workoutId + ", programName=" + programName +
                ", cycleNumber=" + cycleNumber + ", weekNumber=" + weekNumber + ", dayNumber=" +
                dayNumber + ", exerciseName=" + exerciseName + ", exerciseCategory=" +
                exerciseCategory + ", dayExerciseNumber=" + dayExerciseNumber + ", reps=" + reps +
                ", weightPercentage=" + weightPercentage + ", enabled=" + enabled + "]";
    }

    // Setters & getters


    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(int cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getExerciseCategory() {
        return exerciseCategory;
    }

    public void setExerciseCategory(int exerciseCategory) {
        this.exerciseCategory = exerciseCategory;
    }

    public int getDayExerciseNumber() {
        return dayExerciseNumber;
    }

    public void setDayExerciseNumber(int dayExerciseNumber) {
        this.dayExerciseNumber = dayExerciseNumber;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(Double weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}
