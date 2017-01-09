package com.rwardrup.sheiko;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class selectProgram extends AppCompatActivity {

    String selectedProgram;
    Integer selectedProgramId;
    String selectedCycle;
    Integer selectedCycleId;
    String selectedWeek;
    Integer selectedWeekId;
    String selectedDay;
    Integer selectedDayId;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_program);

        // Get sharedpreferences editor
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Read the saved preferences
        selectedProgram = sharedPref.getString("selectedProgram", "29");
        selectedProgramId = sharedPref.getInt("selectedProgramId", 12);
        selectedCycle = sharedPref.getString("selectedCycle", "1");
        selectedCycleId = sharedPref.getInt("selectedCycleId", 1);
        selectedWeek = sharedPref.getString("selectedWeek", "1");
        selectedWeekId = sharedPref.getInt("selectedWeekId", 1);
        selectedDay = sharedPref.getString("selectedDay", "1");
        selectedDayId = sharedPref.getInt("selectedDayId", 1);
        final SharedPreferences.Editor editor = sharedPref.edit();

        Log.i("ProgramLoad", "Loaded: " + selectedCycle);
        Log.i("ProgramLoad", "Loaded Id: " + selectedCycleId);

        // Old programs that you typically see floating around the interwebs
        final String[] oldNumberedPrograms = new String[]{"29", "30", "31", "32", "37", "39", "40"};

        // Set Lifts button -> settings activity
        Button setLiftsButton = (Button) findViewById(R.id.setLiftsButton);
        setLiftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(selectProgram.this, SettingsActivity.class));
            }
        });

        final Spinner programSpinner = (Spinner) findViewById(R.id.programSpinner);
        final Spinner cycleSpinner = (Spinner) findViewById(R.id.cycleSpinner);
        final Spinner weekSpinner = (Spinner) findViewById(R.id.programWeekSpinner);
        final Spinner daySpinner = (Spinner) findViewById(R.id.programDaySpinner);
        final TextView cycleTitleText = (TextView) findViewById(R.id.cycleSelectionTextTitle);

        if (Arrays.asList(oldNumberedPrograms).contains(selectedProgram)) {
            cycleSpinner.setEnabled(false);
        }

        programSpinner.setSelection(selectedProgramId);  // TODO get this value from db
        cycleSpinner.setSelection(selectedCycleId);
        weekSpinner.setSelection(selectedWeekId);
        daySpinner.setSelection(selectedDayId);
        // Listen for changes in program spinner selection

        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user selects one of the old programs (in oldNumberedPrograms array defined
                // above), disable usage of the cycleSPinner since each one of the numbered programs
                // is its own cycle.
                selectedProgram = programSpinner.getSelectedItem().toString();
                selectedProgramId = programSpinner.getSelectedItemPosition();

                Log.d("ProgramSelectionChanged", "Program Selection: " + selectedProgram);
                if (Arrays.asList(oldNumberedPrograms).contains(selectedProgram)) {
                    cycleSpinner.setEnabled(false);
                    cycleTitleText.setEnabled(false);
                } else {
                    cycleSpinner.setEnabled(true);
                    cycleTitleText.setEnabled(true);
                }

                editor.putString("selectedProgram", selectedProgram);
                editor.putInt("selectedProgramId", selectedProgramId);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cycleSpinner.setEnabled(false);
            }
        });

        cycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user selects one of the old programs (in oldNumberedPrograms array defined
                // above), disable usage of the cycleSPinner since each one of the numbered programs
                // is its own cycle.
                selectedCycle = cycleSpinner.getSelectedItem().toString();
                selectedCycleId = cycleSpinner.getSelectedItemPosition();

                editor.putString("selectedCycle", selectedCycle);
                editor.putInt("selectedCycleId", selectedCycleId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user selects one of the old programs (in oldNumberedPrograms array defined
                // above), disable usage of the cycleSPinner since each one of the numbered programs
                // is its own cycle.
                selectedWeek = weekSpinner.getSelectedItem().toString();
                selectedWeekId = weekSpinner.getSelectedItemPosition();

                editor.putString("selectedWeek", selectedWeek);
                editor.putInt("selectedWeekId", selectedWeekId);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user selects one of the old programs (in oldNumberedPrograms array defined
                // above), disable usage of the cycleSPinner since each one of the numbered programs
                // is its own cycle.
                selectedDay = daySpinner.getSelectedItem().toString();
                selectedDayId = daySpinner.getSelectedItemPosition();
                editor.putString("selectedDay", selectedDay);
                editor.putInt("selectedDayId", selectedDayId);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}
