package com.rwardrup.sheiko;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import com.roomorama.caldroid.CaldroidFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CalendarSelectActivity extends FragmentActivity {

    // Date parser to convert date Strings to a format readable by CalDroid
    public Date ParseDate(String date_str) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date dateStr = null;
        try {
            dateStr = formatter.parse(date_str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dateStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_workout_calendar);

        // Get previous workouts from DB
        final MySQLiteHelper db = new MySQLiteHelper(this);
        final List<WorkoutHistory> workoutHistory = db.getAllWorkoutHistory();

        // Initialize the CalDroid calendar
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.selectWorkoutCalendar, caldroidFragment);
        t.commit();




        HashMap hm = new HashMap<>();
        for (int i = 0; i < workoutHistory.size(); i++) {
            String date = workoutHistory.get(i).getDate();
            Log.i("WorkoutCalendar", "Date: " + date);

            // Put dates in hashmap
            hm.put(ParseDate(date), getDrawable(R.color.accent_light));

            if (i == 0) // set min date
                caldroidFragment.setMinDate(ParseDate(date));
        }

        Log.i("WorkoutCalendar", "date hashmap contents: " + hm);

        caldroidFragment.setBackgroundDrawableForDates(hm);
        caldroidFragment.refreshView();

        // TODO: Get workout history & populate calendar with previous & future workouts
        // TODO: Also, show workout summary on bottom portion of activity
    }
}
