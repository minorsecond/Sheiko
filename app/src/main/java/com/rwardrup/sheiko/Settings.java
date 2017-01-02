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
import android.widget.Toast;

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
    private RadioButton lbsButton;
    private RadioButton kgButton;
    private EditText weightInput;
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

        lbsButton = (RadioButton) findViewById(R.id.lbsRadiobutton);
        kgButton = (RadioButton) findViewById(R.id.kgsRadioButton);
        weightInput = (EditText) findViewById(R.id.weightInput);
        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpref.edit();

        // Shared prefs

        Log.i("ReadParameters", "Read the following user parameters: " + bodyweight + ", " +
                sex + ", " +
                unit);

        final ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_settings);

        try {
            bodyweight = sharedpref.getLong("bodyweight", -1);
            //sex = sharedpref.getString("sex", "unset");
            //sexId = sharedpref.getInt("sexId", 1);
            //unit = sharedpref.getString("unit", "lbs");
            //weightInput.setText(bodyweight.toString());
            //sexSpinner.setSelection(sexId);

            if (unit.equals("lbs")) {
                lbsButton.setChecked(true);
            } else {
                kgButton.setChecked(true);
            }

        } catch (NullPointerException e) {
            Log.d("DbReadError", "User parameter DB read error: " + e);  // First creation of database.
        }

        try {
            readFromUserMaxDb(activitySettingsBinding);  // Load databases
        } catch (NullPointerException e) {
            Log.d("DbReadError", "User max DB read error: " + e);  // First creation of database.
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
                saveUserParameters(unit, editor);

                if (activitySettingsBinding.lbsRadiobutton.isChecked()) {
                    unit = "lbs";
                } else {
                    unit = "kg";
                }

                saveToUserMaxDb(activitySettingsBinding);
                Log.d("Database", "Committed data to database");

                startActivity(new Intent(Settings.this, MainActivity.class));
            }
        });
    }

    private void saveUserParameters(String unit, SharedPreferences.Editor editor) {

        Log.i("WriteParameters", "Writing the following parameters: " + bodyweight + ", " +
                sex + ", " + unit);

        editor.putString("unit", unit);
        editor.putLong("bodyweight", Long.parseLong(weightInput.getText().toString()));
        editor.putString("sex", sexSpinner.getSelectedItem().toString());
        editor.putInt("sexId", sexSpinner.getSelectedItemPosition());
        editor.commit();
    }

    // Save user parameters to DB
    private void saveToUserParamDb(ActivitySettingsBinding binding) {
        SQLiteDatabase UserParamDb = new userParametersHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        unit = "kilograms";  // Default to Kg.

        // Get unit value. If the lbs radio button isn't checked, the value is left default (kg)
        if (binding.lbsRadiobutton.isChecked()) {
            unit = "pounds";
        } else {
            unit = "kilograms";
        }

        // Commit the data to the UserParameters table
        Log.d("Weight input: ", "value: " + binding.weightInput.getText().toString());
        values.put(userData.UserParameters.USER_UNITS, unit);
        values.put(userData.UserParameters.BODY_WEIGHT, binding.weightInput.getText().toString());
        values.put(userData.UserParameters.SEX, binding.genderSpinner.getSelectedItem().toString());

        // Get a row ID and print it
        long newRowId = UserParamDb.insert(userData.UserParameters.TABLE_NAME, null, values);
        Toast.makeText(this, "Your settings have been saved", Toast.LENGTH_LONG).show();
        Log.d("SQL: ", "String: " + values);
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
            squat_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.SQUAT_MAX)));
            bench_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.BENCH_MAX)));
            deadlift_max = Integer.parseInt(cursor.getString(cursor.getColumnIndex(userData.UserMaxes.DEADLIFT_MAX)));
        }
        cursor.close();

        // Write the DB values to text entry fields
        binding.squatMax.setText(String.valueOf(squat_max));
        binding.benchMax.setText(String.valueOf(bench_max));
        binding.dlMax.setText(String.valueOf(deadlift_max));
    }

    // Create user parameter DB
    public class userParametersHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "testing_database";  // TODO: change this
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

        public static final String DATABASE_NAME = "testing_database";  // TODO: change this
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
