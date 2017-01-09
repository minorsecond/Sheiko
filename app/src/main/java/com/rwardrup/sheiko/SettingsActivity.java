package com.rwardrup.sheiko;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.firebase.crash.FirebaseCrash;
import com.rwardrup.sheiko.databinding.ActivitySettingsBinding;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {

    //public ActivitySettingsBinding activitySettingsBinding;
    private String unit;
    private Long bodyweight;
    private String sex;
    private int sexId;
    private Double squat_max;
    private Double bench_max;
    private Double deadlift_max;
    private String max_date;
    private Double wilks;
    private EditText weightInput;
    private RadioButton lbsButton;
    private RadioButton kgButton;
    private Spinner sexSpinner;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up DB connection
        final MySQLiteHelper db = new MySQLiteHelper(this);
        UserMaxEntry userMaxEntry = db.getLastUserMaxEntry();
        Log.d("UserMaxEntry", "Latest user max entry: " + userMaxEntry);

        final RadioButton lbsButton = (RadioButton) findViewById(R.id.lbsRadiobutton);
        kgButton = (RadioButton) findViewById(R.id.kgsRadioButton);
        sexSpinner = (Spinner) findViewById(R.id.genderSpinner);
        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpref.edit();

        final ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_settings);

        // Update the Lift Numbers EditText widgets

        try {

            squat_max = userMaxEntry.getSquatMax();
            bench_max = userMaxEntry.getBenchMax();
            deadlift_max = userMaxEntry.getDeadliftMax();

            activitySettingsBinding.squatMax.setText(String.valueOf(squat_max));
            activitySettingsBinding.benchMax.setText(String.valueOf(bench_max));
            activitySettingsBinding.dlMax.setText(String.valueOf(deadlift_max));
        } catch (NullPointerException e) {
            // Probably loaded without having entered anything
        }

        try {
            bodyweight = sharedpref.getLong("bodyweight", -1);
            sex = sharedpref.getString("sex", "unset");
            sexId = sharedpref.getInt("sexId", 1);
            unit = sharedpref.getString("unit", "kilograms");
            activitySettingsBinding.genderSpinner.setSelection(sexId);

            try {
                if (unit.equals("pounds")) {
                    activitySettingsBinding.lbsRadiobutton.setChecked(true);
                } else {
                    activitySettingsBinding.kgsRadioButton.setChecked(true);
                }
            } catch (NullPointerException e) {
                unit = "kilograms";
                Log.d("ParameterLoadError", "Couldn't load unit. Defaulted to kg.");
            }

            Log.i("ReadParameters", "Read the following parameters: " + bodyweight);

            if (bodyweight > 0) {
                activitySettingsBinding.weightInput.setText(Long.toString(bodyweight));
            } else {
                activitySettingsBinding.weightInput.setText("");
            }

        } catch (NullPointerException e) {
            Log.d("DbReadError", "User parameter DB read error: " + e);  // First creation of database.
        }

        try {
            //readFromUserMaxDb(activitySettingsBinding);  // Load databases
        } catch (NullPointerException e) {
            Log.d("DbReadError", "User max DB read error: " + e);  // First creation of database.
            FirebaseCrash.report(new Exception("DbReadError: " + e));
        }

        // Handle user clicking on the timer settings button
        activitySettingsBinding.timerSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RestDurationPicker().show(getFragmentManager(), "Session break length");
            }
        });

        // Save to the DB when user clicks the save button.
        activitySettingsBinding.saveSettings.setOnClickListener(new View.OnClickListener() {
            // TODO: save to firebase as well
            @Override
            public void onClick(View v) {
                saveUserParameters(activitySettingsBinding, unit, editor);

                if (activitySettingsBinding.lbsRadiobutton.isChecked()) {
                    unit = "pounds";
                } else {
                    unit = "kilograms";
                }

                //saveToUserMaxDb(activitySettingsBinding); //old method

                squat_max = Double.valueOf(activitySettingsBinding.squatMax.getText().toString());
                bench_max = Double.valueOf(activitySettingsBinding.benchMax.getText().toString());
                deadlift_max = Double.valueOf(activitySettingsBinding.dlMax.getText().toString());

                db.addUserMaxEntry(new UserMaxEntry(unit, squat_max, bench_max, deadlift_max,
                        wilks, max_date));

                Log.d("Database", "Committed data to database");

                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }

    private void saveUserParameters(ActivitySettingsBinding binding, String unit, SharedPreferences.Editor editor) {

        if (binding.lbsRadiobutton.isChecked()) {
            unit = "pounds";
        } else {
            unit = "kilograms";
        }

        Log.i("WriteParameters", "Writing the following parameters: " + bodyweight + ", " +
                sex + ", " + unit);

        try {
            editor.putString("unit", unit);
            editor.putLong("bodyweight", Long.parseLong(binding.weightInput.getText().toString()));
            editor.putString("sex", binding.genderSpinner.getSelectedItem().toString());
            editor.putInt("sexId", binding.genderSpinner.getSelectedItemPosition());
            editor.commit();
        } catch (Exception e) {
            // User probably tried to save without entering anything
            FirebaseCrash.report(new Exception("DbWriteError: " + e));
            Log.e("DbWriteError", "DB Write error: " + e);
        }
    }
}
