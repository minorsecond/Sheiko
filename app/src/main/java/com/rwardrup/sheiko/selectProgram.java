package com.rwardrup.sheiko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class selectProgram extends AppCompatActivity {

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_program);

        // Old programs that you typically see floating around the interwebs
        final String[] oldNumberedPrograms = new String[]{"29", "30", "31", "32", "37", "39", "40"};

        // Set Lifts button -> settings activity
        Button setLiftsButton = (Button) findViewById(R.id.setLiftsButton);
        setLiftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(selectProgram.this, Settings.class));
            }
        });

        // Listen for changes in program spinner selection
        final Spinner programSpinner = (Spinner) findViewById(R.id.programSpinner);
        final Spinner cycleSpinner = (Spinner) findViewById(R.id.cycleSpinner);

        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If user selects one of the old programs (in oldNumberedPrograms array defined
                // above), disable usage of the cycleSPinner since each one of the numbered programs
                // is its own cycle.
                String selectedProgram = (String) programSpinner.getSelectedItem();

                Log.d("ProgramSelectionChanged", "Program Selection: " + selectedProgram);
                if (Arrays.asList(oldNumberedPrograms).contains(selectedProgram)) {
                    cycleSpinner.setEnabled(false);
                } else {
                    cycleSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cycleSpinner.setEnabled(false);
            }
        });
    }
}
