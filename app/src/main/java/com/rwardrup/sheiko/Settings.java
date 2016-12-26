package com.rwardrup.sheiko;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rwardrup.sheiko.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {

    //public ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_settings);

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

        // This table should really only have one row, so delete all previous rows before saving
        // new user settings.
        UserParamDb.execSQL("DELETE FROM " + userData.UserParameters.TABLE_NAME);

        // Get the entered data
        Log.d("Weight input: ", "value: " + binding.weightInput.getText().toString());
        values.put(userData.UserParameters.BODY_WEIGHT, binding.weightInput.getText().toString());
        values.put(userData.UserParameters.SEX, binding.genderSpinner.getSelectedItem().toString());

        // Get a row ID and print it
        long newRowId = UserParamDb.insert(userData.UserParameters.TABLE_NAME, null, values);
        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();

        Log.d("SQL CMD: ", "String: " + values);
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
