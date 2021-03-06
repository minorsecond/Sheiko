package com.rwardrup.sheiko;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CalendarSelectActivity extends FragmentActivity {

    AlertDialog editCalendarDate;
    String workoutDate = "";
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    List<WorkoutHistory> workoutHistory;

    // Date parser to convert date Strings to a format readable by CalDroid
    public Date ParseDate(String date_str) {
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

        // Initialize the CalDroid calendar
        final CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        // Calendar selection confirmation dialog
        // TODO: This will take user to TrainActivity where they will be able to adjust
        // previous sets and reps.
        editCalendarDate = new AlertDialog.Builder(CalendarSelectActivity.this).
                setNegativeButton("No", null).setPositiveButton("Yes", null).
                setMessage("Are you sure you want to edit the workout for this date?").create();

        editCalendarDate.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent trainIntent = new Intent(CalendarSelectActivity.this, TrainActivity.class);
                Bundle b = new Bundle();    // This bundle will store the workoutRow int,
                                            // Which refers to the row the TrainActivity should
                                            // open up to
                b.putString("workoutDate", workoutDate);
                trainIntent.putExtras(b);
                startActivity(trainIntent);
            }
        });

        // Get previous workouts from DB
        final MySQLiteHelper db = new MySQLiteHelper(this);
        workoutHistory = db.getAllWorkoutHistory();

        // Add the attributes to the calendar
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.selectWorkoutCalendar, caldroidFragment);
        t.commit();

        // Iterate through the prior workouts and add them to the calendar via a hashmap
        HashMap hm = new HashMap<>();
        for (int i = 0; i < workoutHistory.size(); i++) {
            String date = workoutHistory.get(i).getDate();  // get workout date
            Log.i("WorkoutCalendar", "Date: " + date);

            // Put dates in hashmap
            hm.put(ParseDate(date), getDrawable(R.color.accent_light));  // put in map with color

            if (i == 0) // set min date
                caldroidFragment.setMinDate(ParseDate(date));
        }

        // Add the dates and colors to the calendar
        Log.i("WorkoutCalendar", "date hashmap contents: " + hm);
        caldroidFragment.setBackgroundDrawableForDates(hm);
        caldroidFragment.refreshView(); // refresh calendar view

        caldroidFragment.setCaldroidListener(listener);

        // TODO: Also, show workout summary (stats) on bottom portion of activity
    }

    final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            //editCalendarDate.show();
        }

        @Override
        public void onLongClickDate(Date date, View view) {

            try {
                workoutDate = formatter.format(date);
                Log.i("LongPress", "Long pressed on " + workoutDate);
            } catch (Exception e) {
                Log.e("DateParseException", "Date parse exception: "+ e);
            }

            Log.i("WorkoutHistory", "Workout history: "+ workoutHistory);
            int i = 0;
            boolean dateMatch = false;
            while (i < workoutHistory.size() && !dateMatch) {
                Log.i("date", "date=" + workoutHistory.get(i).getDate());
                if (workoutHistory.get(i).getDate().equals(workoutDate)) {
                    dateMatch = true;
                    editCalendarDate.show();
                    Log.i("MatcheDate", "Matched dates");
                }
                i += 1;
            }
        }
    };
}
