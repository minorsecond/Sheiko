package com.rwardrup.sheiko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rwardrup.sheiko.databinding.ActivitySettingsBinding;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Settings extends AppCompatActivity {

    //public ActivitySettingsBinding activitySettingsBinding;
    public String unit;
    public String bodyweight;
    public String sex;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_settings);

        try {
            readFromUserParamDb(activitySettingsBinding);  // Load database
        } catch (java.lang.NullPointerException e) {
            Log.d("DbReadError", "DB Read Error: " + e);  // First creation of database.
        }

        // Save to the DB when user clicks the save button.
        activitySettingsBinding.saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Settings.this, MainActivity.class));
                saveToUserParamDb(activitySettingsBinding);
            }
        });
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

        // This table should really only have one row, so delete all previous rows before saving
        // new user settings.
        //UserParamDb.execSQL("DELETE FROM " + userData.UserParameters.TABLE_NAME);

        // Commit the data to the UserParameters table
        Log.d("Weight input: ", "value: " + binding.weightInput.getText().toString());
        values.put(userData.UserParameters.USER_UNITS, unit);
        values.put(userData.UserParameters.BODY_WEIGHT, binding.weightInput.getText().toString());
        values.put(userData.UserParameters.SEX, binding.genderSpinner.getSelectedItem().toString());

        // Get a row ID and print it
        long newRowId = UserParamDb.insert(userData.UserParameters.TABLE_NAME, null, values);
        Toast.makeText(this, "Your settings have been saved", Toast.LENGTH_LONG).show();

        Log.d("SQL CMD: ", "String: " + values);
    }

    // Read from user Parameters DB
    private void readFromUserParamDb(ActivitySettingsBinding binding) {
        SQLiteDatabase database = new userParametersHelper(this).getReadableDatabase();

        // Get the last user parameter entry
        String[] columns = {
                "units",
                "body_weight",
                "sex"
        };

        Cursor cursor = database.query(userData.UserParameters.TABLE_NAME, columns, null, null, null, null, null);

        Log.d("CursorCount", "The total cursor count is " + cursor.getColumnCount());

        // Get values from DB cursor
        if (cursor.moveToLast()) {
            unit = cursor.getString(cursor.getColumnIndex(userData.UserParameters.USER_UNITS));
            bodyweight = cursor.getString(cursor.getColumnIndex(userData.UserParameters.BODY_WEIGHT));
            sex = cursor.getString(cursor.getColumnIndex(userData.UserParameters.SEX));
        }
        cursor.close();

        // Write the DB values to text entry fields
        Log.d("UnitFromDB:", "Unit found in DB: " + unit);
        binding.weightInput.setText(bodyweight);
        if (unit.equals("pounds")) {
            binding.kgsRadioButton.setChecked(false);
            binding.lbsRadiobutton.setChecked(true);
        } else {
            binding.kgsRadioButton.setChecked(true);
            binding.lbsRadiobutton.setChecked(false);
        }

        if (sex.equals("Female")) {
            binding.genderSpinner.setSelection(1);
        } else {
            binding.genderSpinner.setSelection(0);
        }
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + userData.UserParameters.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
