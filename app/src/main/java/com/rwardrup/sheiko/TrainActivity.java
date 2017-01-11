package com.rwardrup.sheiko;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TrainActivity extends AppCompatActivity implements RestDurationPicker.DurationListener {

    private static long millisLeftOnTimer;
    private static Integer secondsLeftOnTimer;
    // For display and db retrieval
    String currentProgram;
    String currentCycle;
    String currentCycleText;
    String currentWeek;
    String currentDay;
    String currentWorkoutText;
    Spinner workoutSelectionSpinner;
    Button startBreakTimerButton;
    Button stopBreakTimerButton;
    Button pauseBreakTimerButton;
    Button nextSetButton;
    Button previousSetButton;
    // Activity buttons
    ImageButton squatSelectButton;
    ImageButton deadliftSelectButton;
    ImageButton benchSelectButton;
    Spinner accessorySpinner;
    Switch autoTimerSwitch;
    Boolean autoTimerEnabled = false;  // TODO: get this from shared preferences
    // Text output
    TextView breakTimerOutput;
    TextView breakTimerTab;
    TextView currentExercise;
    TextView currentWorkout;
    CrystalSeekbar alarmVolumeControl;
    String current_exercise_string = "squat"; // TODO: Set this depending on first exercise of day
    AlertDialog changeSetPrompt;
    ProgramDbHelper programDbHelper;
    private NumberPicker repPicker;
    private NumberPicker weightPicker;
    private SharedPreferences.Editor editor;
    private boolean viewingPastSet = false;
    // Timer stuff
    private Integer timerDurationSeconds;  // 3 minutes is a good default value
    private boolean activityLoaded = false;
    private AudioManager audioManager;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };
    private int currentVolume;
    private int workoutHistoryRow = 0;
    private int setNumber = 0;
    private int moveBetweenSetsCounter = 0;
    private TextView setDisplay;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        // Set changer prompt
        changeSetPrompt = new AlertDialog.Builder(TrainActivity.this).setNegativeButton("Cancel",
                null).create();

        changeSetPrompt.setTitle("Previous set has been edited");
        changeSetPrompt.setMessage("Previous set data has been edited. Do you want " +
                "to save these changes?");

        setDisplay = (TextView) findViewById(R.id.setsDisplay);
        setDisplay.setText("Set " + String.valueOf(setNumber + 1) + " of 14");

        // Get today's date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final String date = df.format(c.getTime());

        Log.i("TodaysDate", "Today's date: " + date);

        final MySQLiteHelper db = new MySQLiteHelper(this);

        /**
         * CRUD Operations
         **/

        List<WorkoutProgram> todaysWorkout = db.getTodaysWorkout("Advanced Medium Load",
                1, 1, 1);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Shared prefs
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        // Set up volume control
        alarmVolumeControl = (CrystalSeekbar) findViewById(R.id.volumeController);
        currentVolume = sharedPref.getInt("alarmVolume", 4);  // Try to get last set volume
        Log.i("VolumeControl", "Initial volume position: " + currentVolume);
        alarmVolumeControl.setMinStartValue(currentVolume).apply(); // Set the bar at the last vol position

        // Set up the seekbar change listener
        alarmVolumeControl.setOnSeekbarFinalValueListener(new OnSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number value) {
                currentVolume = value.intValue();
                editor.putInt("alarmVolume", currentVolume);
                editor.commit();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, -1);
                Log.i("TimerVolume", "Timer volume changed to " + currentVolume);
            }
        });

        final String[] oldNumberedPrograms = new String[]{"29", "30", "31", "32", "37", "39", "40"};

        // TODO: Programmatically set the array of today's accessories based on the sqlite db row
        String[] todaysAccessories = new String[]{"French Press", "Pullups", "Abs", "Bent-Over Rows",
                "Seated Good Mornings", "Good Mornings", "Hyperextensions", "Dumbell Flys"};

        // Hide the accessory spinner text
        accessorySpinner = (Spinner) findViewById(R.id.accessorySpinner);
        CustomAdapter<String> accessorySpinnerAdapter = new CustomAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, todaysAccessories);
        accessorySpinner.setAdapter(accessorySpinnerAdapter);

        // Try to get the timer duration from shared preferences, defaulting to 1.5 minutes if it
        // hasn't been set. Time is stored in milliseconds in sharedPreferences. The time is saved
        // in the sharedPreferences file in RestDurationPicker.
        timerDurationSeconds = (int) (long) (sharedPref.getLong("timerDuration", 90000) / 1000);
        currentProgram = sharedPref.getString("selectedProgram", "Advanced Medium Load");
        currentCycle = sharedPref.getString("selectedCycle", "1");
        currentWeek = sharedPref.getString("selectedWeek", "1");
        currentDay = sharedPref.getString("selectedDay", "1");

        if (Arrays.asList(oldNumberedPrograms).contains(currentProgram)) {
            currentCycleText = "";
        } else {
            currentCycleText = " (" + currentCycle + ") ";
        }


        // Build the string for current workout display
        currentWorkoutText = currentProgram + currentCycleText + " - " + "Week " +
                currentWeek + " " + "Day " + currentDay;

        // For passing the timer duration to the timer service
        final Intent sendDuration = new Intent("getting_data");

        startBreakTimerButton = (Button) findViewById(R.id.startBreakTimer);
        stopBreakTimerButton = (Button) findViewById(R.id.stopBreakButton);
        pauseBreakTimerButton = (Button) findViewById(R.id.pauseBreakButton);
        squatSelectButton = (ImageButton) findViewById(R.id.squatSelectButton);
        benchSelectButton = (ImageButton) findViewById(R.id.benchSelectButton);
        deadliftSelectButton = (ImageButton) findViewById(R.id.deadliftButton);
        autoTimerSwitch = (Switch) findViewById(R.id.autoTimerSwitch);
        nextSetButton = (Button) findViewById(R.id.nextSetButton);
        previousSetButton = (Button) findViewById(R.id.previousSetButton);

        breakTimerOutput = (TextView) findViewById(R.id.breakTimerOutput);
        breakTimerTab = (TextView) findViewById(R.id.timerTabTitle);
        currentExercise = (TextView) findViewById(R.id.currentExerciseDisplay);
        currentWorkout = (TextView) findViewById(R.id.currentWorkoutDisplay);

        currentWorkout.setText(currentWorkoutText);

        // Set the reps and weights
        setRepsWeightPickers();

        // Listen for changes in rep numberPicker
        repPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (viewingPastSet) {
                }
            }
        });

        weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (viewingPastSet) {
                }
            }
        });

        // This is an example of how changing images to active/inactive versions
        // will be done programmatically
        squatSelectButton.setImageResource(R.drawable.squats);

        this.squatSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_exercise_string = "squat";
                currentExercise.setText(R.string.squat);
            }
        });

        this.benchSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText(R.string.bench);
                current_exercise_string = "bench";
            }
        });

        this.deadliftSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText(R.string.deadlift);
                current_exercise_string = "deadlift";
            }
        });

        // Run the nextSet button alongside the autotimer listener.
        this.nextSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setNumber == moveBetweenSetsCounter) {  // If user is at current set
                    viewingPastSet = false;
                    Log.i("NewSet", "SetNumber=" + setNumber + ", moveBetweenSetsCounter=" + moveBetweenSetsCounter);

                    // reset timer if user goes to next set before it reaches 0
                    if (autoTimerEnabled) {
                        Intent timerService = new Intent(TrainActivity.this, BreakTimer.class);
                        if (millisLeftOnTimer > 0) {
                            stopService(new Intent(TrainActivity.this, BreakTimer.class));
                            startService(timerService);
                        } else {
                            startService(timerService);
                        }
                    }

                    // Commit the current repPicker and weightPicker values to Workout history table
                    // 1. Get repPicker current value
                    String workoutId = String.valueOf(db.getWorkoutHistoryRowCount());
                    int currentReps = repPicker.getValue();
                    Double currentWeight = Double.valueOf((weightPicker.getValue() + 1) * 5);
                    Log.i("SetSaved", "Current reps: " + currentReps + ", " +
                            "current weight: " + currentWeight);

                    db.addWorkoutHistory(new WorkoutHistory(workoutId, date, current_exercise_string,
                            currentReps, currentWeight, currentProgram));

                    workoutHistoryRow = db.getWorkoutHistoryRowCount();

                    Log.d("Database", "Committed workout history to database. There are now " + workoutHistoryRow + " rows.");
                    setNumber += 1;
                    moveBetweenSetsCounter = setNumber;

                    setDisplay.setText("Set " + (setNumber + 1) + " of 14");
                } else if (setNumber > moveBetweenSetsCounter) { // Go forward in history
                    Log.i("NextSetInHistory", "SetNumber=" + setNumber + ", moveBetweenSetsCounter=" + moveBetweenSetsCounter);
                    final int currentDbRow = (workoutHistoryRow - (setNumber - moveBetweenSetsCounter));
                    Log.i("NextSetInHistory", "Moving to row " + currentDbRow);
                    WorkoutHistory nextSet = db.getWorkoutHistory(currentDbRow);
                    int reps = nextSet.getReps();
                    int weight = nextSet.getWeight().intValue();

                    // TODO: Enable changing previous sets by calling db.changeWorkoutHistoryAtId()
                    // Do current rep and weight picker values match what's in the table?
                    Log.i("Forward", "repPicker value=" + repPicker.getValue() + " db reps value=" + reps +
                            " weightPicker value=" + (weightPicker.getValue() + 1) * 5 + " db weight value=" + weight);

                    if (repPicker.getValue() != reps || !Double.valueOf((weightPicker.getValue() + 1) * 5).equals(Double.valueOf(weight))) {

                        // update the table
                        int new_reps = repPicker.getValue();
                        Double new_weight = Double.valueOf((weightPicker.getValue() + 1) * 5);

                        final WorkoutHistory changedWorkoutHistory = new WorkoutHistory("0", date,
                                current_exercise_string, new_reps, new_weight, currentProgram);

                        Log.i("ChangedWorkoutHistory", "New row: " + changedWorkoutHistory + " at " +
                                "row " + currentDbRow);

                        Log.i("ChangedWorkoutHistory", "old reps=" + reps + " old weight=" + Double.valueOf(weight)
                                + " new reps=" + new_reps + " new weight=" + Double.valueOf(new_weight));

                        changeSetPrompt.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Changed previous set",
                                        Toast.LENGTH_SHORT).show();

                                db.changeWorkoutHistoryAtId(currentDbRow, changedWorkoutHistory);
                            }
                        });

                        // Show the prompt
                        changeSetPrompt.show();
                    }

                    moveBetweenSetsCounter += 1;
                    Log.i("NextSetInHistory", "SetNumber=" + setNumber + ", moveBetweenSetsCounter=" + moveBetweenSetsCounter);

                    nextSet = db.getWorkoutHistory((workoutHistoryRow - (setNumber - moveBetweenSetsCounter)));
                    reps = nextSet.getReps();
                    weight = nextSet.getWeight().intValue();

                    repPicker.setValue(reps);
                    weightPicker.setValue((weight - 1) / 5);
                    setDisplay.setText("Set " + (moveBetweenSetsCounter + 1) + " of 14");
                }
            }
        });

        this.previousSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement set-change code here too.
                // Go back to last-entered set if it exists
                if (moveBetweenSetsCounter > 1 && moveBetweenSetsCounter <= setNumber) {
                    moveBetweenSetsCounter -= 1;
                    viewingPastSet = true;
                    Log.i("MoveToOldSet", "Set number: " + setNumber + " moveBetweenSetsCounter=" + moveBetweenSetsCounter);
                    // Get previous workout history row
                    int currentDbRow = (workoutHistoryRow - (setNumber - moveBetweenSetsCounter));
                    Log.i("MoveBetweenSets", "Getting row number " + currentDbRow + " of" + workoutHistoryRow);
                    WorkoutHistory lastSet = db.getWorkoutHistory(currentDbRow);
                    Log.i("WorkoutHistory", lastSet.toString());
                    int reps = lastSet.getReps();
                    int weight = lastSet.getWeight().intValue();
                    repPicker.setValue(reps);
                    weightPicker.setValue((weight - 1) / 5);

                    setDisplay.setText("Set " + (moveBetweenSetsCounter + 1) + " of 14");

                    Log.i("MoveBetweenSets", "Previous set values: reps=" + reps + ", weight=" +
                            weight / 5 + " at set number " + moveBetweenSetsCounter);
                }
            }
        });

        this.accessorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selectedAccessory = arg0.getSelectedItem().toString();
                Log.i("Selection", "Accessory was selected: " + selectedAccessory);

                // If user has selected an accessory, update the current activity. This prevents
                // The current activity from being set to the acessory on Activity load.
                if (activityLoaded) {
                    currentExercise.setText(selectedAccessory);
                    Log.i("Selection", "Selection set.");
                } else {
                    activityLoaded = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //do nothing
            }
        });

        // Autotimer switch
        this.autoTimerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoTimerSwitch.isChecked()) {
                    autoTimerEnabled = true;
                    Log.i("Timer", "auto timer enabled");
                } else {
                    autoTimerEnabled = false;
                    Log.i("Timer", "auto timer disabled");
                }
            }
        });

        currentWorkout.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(TrainActivity.this, selectProgram.class));
                return true;
            }
        });


        // TODO: Handle accesssory change -> updated current workout text

        // Handle user long-clicking on the timer output text to change timer length on-the-fly
        // This utilizes the onDurationSet method at the bottom of this class.
        breakTimerOutput.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new RestDurationPicker().show(getFragmentManager(), "Session break length");
                return true;
            }
        });

        // Set timer display TODO: Get this to keep incrementing dimer display off-activity
        breakTimerOutput.setText(secondsToString(timerDurationSeconds));
        breakTimerTab.setText("Rest Timer " + secondsToString(timerDurationSeconds));

        // break timer start / stop
        startBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            Intent timerService = new Intent(TrainActivity.this, BreakTimer.class);

            @Override
            public void onClick(View v) {
                breakTimerOutput.setTextSize(36);
                // Set up service for timer
                startService(timerService);
                Log.i("TimerService", "Started timer service");
                }
        });

        // break timer stop
        stopBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                millisLeftOnTimer = 0;
                //mCountDownTimer.cancel();
                stopService(new Intent(TrainActivity.this, BreakTimer.class));
                breakTimerOutput.setTextSize(36);
                breakTimerOutput.setText(secondsToString(timerDurationSeconds));
                breakTimerTab.setText("Rest Timer - " + secondsToString(timerDurationSeconds));
                }
        });

        // Load workout history database here TODO: write this in actual code
        // lastWorkout = workoutHistoryDb.LastWorkout()
        // if (lastWorkout > 0 && lastWorkout < 4) {
        //  nextWorkout = lastWorkout + 1
        // } else if (lastWorkout == 4 && currentWeek < 4) {
        //  nextWorkout = 1
        //  currentWeek += 1
        // } else if (lastWorkout == 4 && currentWeek == 4 && !currentCycle.equals("Competition")) {
        //  nextWorkout = 1
        //  currentWeek = 1
        //  currentCycle += 1
        // } else if (lastWorkout == 4 && currentWeek == 4 && currentCycle.equals("Competition")) {
        //  nextWorkout = 1
        //  currentWeek = 1
        //  currentCycle = 1
        // }
    }

    // Break timer long-click set time
    @Override
    public void onDurationSet(long duration) {
        Integer i = (int) (long) duration;  // get integer i from duration (long)
        timerDurationSeconds = i / 1000; // convert millis to seconds


        // Assign the new custom timer duration to the timerduration variable
        breakTimerOutput.setText(secondsToString(timerDurationSeconds));
        breakTimerTab.setText("Rest Timer - " + secondsToString(timerDurationSeconds));
        Log.d("NewTimer", "New Timer Duration: " + secondsToString(timerDurationSeconds));
    }

    // convert seconds to minutes and seconds for display
    private String secondsToString(int pTime) {
        return String.format(Locale.US, "%02d:%02d", pTime / 60, pTime % 60);
    }

    // set reps and weight picker contents
    private void setRepsWeightPickers() {

        repPicker = (NumberPicker) findViewById(R.id.repsPicker);
        weightPicker = (NumberPicker) findViewById(R.id.weightPicker);

        // Disable keyboard when numberpicker is selected
        repPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        weightPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Enable fading edges
        repPicker.setVerticalFadingEdgeEnabled(true);
        weightPicker.setVerticalFadingEdgeEnabled(true);

        // Reps
        repPicker.setMinValue(0);
        repPicker.setMaxValue(20);
        repPicker.setValue(5);

        // Weight
        weightPicker.setMinValue(0);
        weightPicker.setMaxValue(240); //480 * 2.5 == 1200

        int length = 500;
        int step = 5;

        String[] numbers = new String[length];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = String.valueOf((i + 1) * step);
        }

        weightPicker.setDisplayedValues(numbers);

        // TODO: Pull this start value from the DB of today's workout, per lift
        weightPicker.setValue(50);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BreakTimer.COUNTDOWN_BR));
        Log.i("BreakTimer", "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            Log.e("TimerError", "timer encountered: " + e);  // User probably closed during pause
        }
        Log.i("BreakTimer", "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            millisLeftOnTimer = millisUntilFinished;
            secondsLeftOnTimer = (int) (long) millisUntilFinished / 1000;
            breakTimerOutput.setText(secondsToString(secondsLeftOnTimer));
            breakTimerTab.setText("Rest Timer - " + secondsToString(secondsLeftOnTimer));
            Log.i("UpdatingGui", "Countdown seconds remaining: " + millisUntilFinished / 1000);
        }
    }

    // Adapter for accessory spinner. Want to hide the display string and dynamically update it
    // when the user changes workouts. Hide the first entry which is an empty string that prevents
    // the currentExercise from being displayed on load.
    private static class CustomAdapter<T> extends ArrayAdapter<String> {

        public CustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("");
            return view;
        }
    }
}