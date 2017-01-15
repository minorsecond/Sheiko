package com.rwardrup.sheiko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_workout_calendar);

        // Get previous workouts from DB
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final List<WorkoutHistory> workoutHistory = db.getAllWorkoutHistory();

        CalendarView workoutCalendar = (CalendarView) findViewById(R.id.selectWorkoutCalendar);

        // TODO: Get workout history & populate calendar with previous & future workouts
        // TODO: Also, show workout summary on bottom portion of activity
    }
}
