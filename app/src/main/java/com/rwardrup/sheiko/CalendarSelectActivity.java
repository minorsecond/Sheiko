package com.rwardrup.sheiko;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarSelectActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_workout_calendar);

        // Get previous workouts from DB
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final List<WorkoutHistory> workoutHistory = db.getAllWorkoutHistory();

        //CalendarView workoutCalendar = (CalendarView) findViewById(R.id.selectWorkoutCalendar);

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.selectWorkoutCalendar, caldroidFragment);
        t.commit();

        for (int i = 0; i < workoutHistory.size(); i++) {
            Log.i("WorkoutCalendar", "Date: " + workoutHistory.get(i).getDate());
        }

        // TODO: Get workout history & populate calendar with previous & future workouts
        // TODO: Also, show workout summary on bottom portion of activity
    }
}
