package com.rwardrup.sheiko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

public class CalendarSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_workout_calendar);

        CalendarView workoutCalendar = (CalendarView) findViewById(R.id.selectWorkoutCalendar);

        // TODO: Get workout history & populate calendar with previous & future workouts
        // TODO: Also, show workout summary on bottom portion of activity
    }
}
