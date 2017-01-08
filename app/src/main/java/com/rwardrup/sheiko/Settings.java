package com.rwardrup.sheiko;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

public class Settings extends AppCompatActivity {

    //public ActivitySettingsBinding activitySettingsBinding;
    private String unit;
    private Long bodyweight;
    private String sex;
    private int sexId;
    private int squat_max;
    private int bench_max;
    private int deadlift_max;
    private int max_date;
    private int wilks;
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

        final RadioButton lbsButton = (RadioButton) findViewById(R.id.lbsRadiobutton);
        kgButton = (RadioButton) findViewById(R.id.kgsRadioButton);
        sexSpinner = (Spinner) findViewById(R.id.genderSpinner);
        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpref.edit();

        final ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_settings);

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
            readFromUserMaxDb(activitySettingsBinding);  // Load databases
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
            @Override
            public void onClick(View v) {
                saveUserParameters(activitySettingsBinding, unit, editor);

                if (activitySettingsBinding.lbsRadiobutton.isChecked()) {
                    unit = "pounds";
                } else {
                    unit = "kilograms";
                }

                saveToUserMaxDb(activitySettingsBinding);
                Log.d("Database", "Committed data to database");

                startActivity(new Intent(Settings.this, MainActivity.class));
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

    // Save user maxes to DB
    private void saveToUserMaxDb(ActivitySettingsBinding binding) {
        SQLiteDatabase userMaxDb = new userParametersHelper(this).getWritableDatabase();
        Log.d("SQL", "Got new userMaxes db");
        ContentValues maxValues = new ContentValues();

        // Commit the data to the UserMaxes table
        try {
            maxValues.put(userData.UserMaxes.USER_UNITS, unit);
            maxValues.put(userData.UserMaxes.SQUAT_MAX, Integer.parseInt(binding.squatMax.getText().toString()));
            maxValues.put(userData.UserMaxes.BENCH_MAX, Integer.parseInt(binding.benchMax.getText().toString()));
            maxValues.put(userData.UserMaxes.DEADLIFT_MAX, Integer.parseInt(binding.dlMax.getText().toString()));
        } catch (NumberFormatException e) {
            // User probably just didn't enter anything in the max fields
        }

        long newRowId = userMaxDb.insert(userData.UserMaxes.TABLE_NAME, null, maxValues);
        Log.d("SQL", "Wrote new maxes to userMaxes table");
    }

    // Read from user User Max DB
    private void readFromUserMaxDb(ActivitySettingsBinding binding) {
        SQLiteDatabase database = new userMaxesHelper(this).getReadableDatabase();

        // Get the last user parameter entry
        String[] columns = {
                "units",
                "squat_max",
                "bench_max",
                "deadlift_max",
                "date",
                "wilks"
        };

        Cursor cursor = database.query(userData.UserMaxes.TABLE_NAME, columns, null, null, null, null, null);
        Log.d("CursorCount", "The total cursor count is " + cursor.getColumnCount());

        // Get values from DB cursor
        if (cursor.moveToLast()) {
            try {
                squat_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.SQUAT_MAX)));
                bench_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.BENCH_MAX)));
                deadlift_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.DEADLIFT_MAX)));
            } catch (Exception e) {
                // DB Was empty. User probably previously didn't enter anything before saving
                FirebaseCrash.report(new Exception("DbReadError: " + e));
                Log.e("DbReadError", "Db read error: " + e);
            }
        }
        cursor.close();

        // Write the DB values to text entry fields
        if (squat_max > 0) {
            binding.squatMax.setText(String.valueOf(squat_max));
        } else {
            binding.squatMax.setText("");
        }

        if (bench_max > 0) {
            binding.benchMax.setText(String.valueOf(bench_max));
        } else {
            binding.benchMax.setText("");
        }

        if (deadlift_max > 0) {
            binding.dlMax.setText(String.valueOf(deadlift_max));
        } else {
            binding.dlMax.setText("");
        }
    }

    // Create user parameter DB
    public class userParametersHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "SheikoDb";  // TODO: change this
        private static final int DATABASE_VERSION = 1;

        public userParametersHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(userData.UserParameters.CREATE_TABLE);
            sqLiteDatabase.execSQL(userData.UserMaxes.CREATE_TABLE);
            Log.d("SQL", "Created userMaxes table");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + userData.UserParameters.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    // Create user maxes DB
    public class userMaxesHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "SheikoDb";  // TODO: change this
        private static final int DATABASE_VERSION = 1;

        public userMaxesHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(userData.UserMaxes.CREATE_TABLE);
            Log.d("SQL", "Created userMaxes table");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + userData.UserMaxes.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
