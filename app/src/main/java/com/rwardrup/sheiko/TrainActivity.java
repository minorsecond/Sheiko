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
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.crash.FirebaseCrash;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TrainActivity extends AppCompatActivity implements RestDurationPicker.DurationListener {

    private static long millisLeftOnTimer;
    private static Integer secondsLeftOnTimer;
    // For display and db retrieval
    String currentProgram;
    Integer currentCycle;
    String currentCycleText;
    Integer currentWeek;
    Integer currentDay;
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
    List<WorkoutSet> todaysWorkout;
    boolean inAWorkout = false;
    private NumberPicker repPicker;
    private NumberPicker weightPicker;
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
    private WorkoutSet currentSet;
    private boolean repsChanged = false;
    private boolean weightChanged = false;
    // Lift max variables
    private Double squat_max;
    private Double bench_max;
    private Double deadlift_max;
    private Double currentWeight;
    private Double roundingValue; // Unit rounding value
    private Boolean accessoryWeightSet;
    private Boolean newExercise = false;
    private Integer currentExerciseNumber = 1;
    private Integer currentSetDisplayNumber = 1;
    private boolean todaysWorkoutLoaded = false;
    private boolean dupeBench = false;
    private boolean dupeSquats = false;
    private boolean dupeDeadlifts = false;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        // Exercise select buttons
        // TODO: Change these to spinners if there are more than 1 exercise per workout
        // category (is this possible?) Also, change the names to exercise1, 2 & 3.
        squatSelectButton = (ImageButton) findViewById(R.id.squatSelectButton);
        benchSelectButton = (ImageButton) findViewById(R.id.benchSelectButton);
        deadliftSelectButton = (ImageButton) findViewById(R.id.deadliftButton);
        accessorySpinner = (Spinner) findViewById(R.id.accessorySpinner);

        // Shared prefs
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPref.edit();

        // TODO: Change these defaults
        currentProgram = sharedPref.getString("selectedProgram", "Advanced Medium Load");
        currentCycle = Integer.valueOf(sharedPref.getString("selectedCycle", "2"));
        currentWeek = Integer.valueOf(sharedPref.getString("selectedWeek", "3"));
        currentDay = Integer.valueOf(sharedPref.getString("selectedDay", "4"));
        inAWorkout = sharedPref.getBoolean("inAWorkout", false);

        // TODO: Get date of last row in WorkoutHistory table. If that date is not today,
        // prompt user to resume/delete workout.

        // Get workout row to open up to, if that exists (int).
        Bundle b = getIntent().getExtras();
        String rowDate = "";
        if (b != null)
            rowDate = b.getString("workoutDate");
            Log.i("GettingWorkoutHistory", "Workout history date: " + rowDate);

        // Set changer prompt
        changeSetPrompt = new AlertDialog.Builder(TrainActivity.this).setNegativeButton("Cancel",
                null).create();

        changeSetPrompt.setTitle("Previous set has been edited");
        changeSetPrompt.setMessage("Previous set data has been edited. Do you want " +
                "to save these changes?");

        setDisplay = (TextView) findViewById(R.id.setsDisplay);
        final FloatingActionButton saveAllSets = (FloatingActionButton) findViewById(R.id.saveAllSets);

        // SaveAllSets button is hidden on app load. It is only needed once user does their first
        // set
        saveAllSets.setVisibility(View.INVISIBLE);

        // Get today's date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final String date = df.format(c.getTime());

        Log.i("TodaysDate", "Today's date: " + date);

        final MySQLiteHelper db = new MySQLiteHelper(this);

        /**
         * CRUD Operations
         **/

        // Get either workout history by date, or todays workout
        if (rowDate != null && rowDate.length() > 0) {
            todaysWorkout = db.getWorkoutHistoryByDate(rowDate);
            todaysWorkoutLoaded = true;
            Toast.makeText(getApplicationContext(), "Workout on " + rowDate,
                    Toast.LENGTH_LONG).show();
            Log.i("TodaysWorkout", "Workout history: " + todaysWorkout);

            // Load the day's workout
        } else {    // TODO: Fix this. this means that the app loads the workout twice, decreasing
            // performance.
            Log.d("GettingWorkout", "Getting cycle " + currentCycle + ", week " + currentWeek + ", " +
                    "day " + currentDay);
            todaysWorkout = db.getTodaysWorkout("Advanced Medium Load", currentCycle, currentWeek, currentDay);
            todaysWorkoutLoaded = true;
        }

        // If user previously left the app before saving the workout, run this code.
        if (inAWorkout) {  // Get last workout row id and use this as the current set
            workoutHistoryRow = db.getWorkoutHistoryRowCount(); // set last row ID
            // TODO: Get workout id of last set completed and load that as todaysWorkout
            final WorkoutHistory lastWorkoutHistoryRow = db.getLastWorkoutHistoryRow();
            if (lastWorkoutHistoryRow.getPersist() == 0) {
                Log.i("ResumeWorkout", "Resuming at set number " + setNumber);
                currentProgram = lastWorkoutHistoryRow.getProgramTableName();

                // If the last unentered row was from today, run this block
                if (!lastWorkoutHistoryRow.getDate().equals(date)) {
                    // This was probably today's workout. Ask user if they want to resume, delete or
                    // save.
                    Log.i("ResumeWorkout", "Found previous sets completed today. Prompting user for action");
                    // Set up resumeWorkoutDialog
                    AlertDialog resumeWorkoutDialog = new AlertDialog.Builder(TrainActivity.this).create();
                    resumeWorkoutDialog.setTitle("Previous Workout Found");

                    // The prompt itself. TODO: Make this 'prettier'
                    resumeWorkoutDialog.setMessage("Action to take on previous unsaved sets from " +
                            lastWorkoutHistoryRow.getDate());

                    // set buttons
                    resumeWorkoutDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save & Start New",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Call saveAllSets code

                                    // Load new workout
                                    todaysWorkout = db.getTodaysWorkout("Advanced Medium Load",
                                            currentCycle, currentWeek, currentDay);
                                    todaysWorkoutLoaded = true;
                                }
                            });

                    resumeWorkoutDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Resume Workout",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currentCycle = lastWorkoutHistoryRow.getCycle();
                                    currentWeek = lastWorkoutHistoryRow.getWeek();
                                    currentDay = lastWorkoutHistoryRow.getDay();

                                    Log.i("ResumeWorkout", "last workout row id = " + lastWorkoutHistoryRow);
                                    Log.i("ResumeWorkout", "last workout row cycle = " + currentCycle);
                                    Log.i("ResumeWorkout", "last workout row week = " + currentWeek);
                                    Log.i("ResumeWorkout", "last workout day = " + currentDay);


                                    todaysWorkout = db.getTodaysWorkout("Advanced Medium Load", currentCycle,
                                            currentWeek, currentDay);

                                    todaysWorkoutLoaded = true;

                                    setNumber = sharedPref.getInt("lastSetNumberCompleted", 1);
                                    currentSetDisplayNumber = setNumber + 1;  // get the last set in table

                                    Log.i("ResumeWorkout", "got set " + setNumber);
                                }
                            });

                    resumeWorkoutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Delete Workout",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Call function to delete workouts with ID matching
                                    // that of the last workout row

                                    db.deleteNonPersistedRows();

                                    // Load new workout
                                    todaysWorkout = db.getTodaysWorkout("Advanced Medium Load",
                                            currentCycle, currentWeek, currentDay);
                                    todaysWorkoutLoaded = true;
                                }
                            });

                    resumeWorkoutDialog.show();
                } else { // Resume workout if it's from the same day
                    currentCycle = lastWorkoutHistoryRow.getCycle();
                    currentWeek = lastWorkoutHistoryRow.getWeek();
                    currentDay = lastWorkoutHistoryRow.getDay();

                    Log.i("ResumeWorkout", "last workout row id = " + lastWorkoutHistoryRow);
                    Log.i("ResumeWorkout", "last workout row cycle = " + currentCycle);
                    Log.i("ResumeWorkout", "last workout row week = " + currentWeek);
                    Log.i("ResumeWorkout", "last workout day = " + currentDay);

                    todaysWorkout = db.getTodaysWorkout("Advanced Medium Load", currentCycle,
                            currentWeek, currentDay);

                    todaysWorkoutLoaded = true;

                    setNumber = sharedPref.getInt("lastSetNumberCompleted", 1);
                    currentSetDisplayNumber = setNumber + 1;  // get the last set in table
                    setDisplay.setText(String.valueOf(currentSetDisplayNumber));

                    Log.i("ResumeWorkout", "got set " + setNumber + " done today.");
                }
            }
        }

        // Just in case something happens, take the nuclear approach and load todaysWorkout if
        // nothing is loaded yet.

        // TODO: This should not appear before the resume workout dialog just above this one.
        if (todaysWorkout == null) {
            String errorMessage = "Warning: Nothing was loaded. Took the nuclear approach & force-" +
                    "loaded the workout.";

            Log.e("TodaysWorkout", errorMessage);
            FirebaseCrash.report(new Exception(errorMessage));

            AlertDialog.Builder warningDialogBuilder = new AlertDialog.Builder(this);
            warningDialogBuilder.setTitle("Warning");
            warningDialogBuilder.setMessage("Something went wrong when attempting to load your previous" +
                    " workouts. The developer has been notified and will fix it ASAP. " +
                    "\n\nIt would be extremely helpful if you were to email him and let him know " +
                    "what you were doing when this happened.");

            warningDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog warningDialog = warningDialogBuilder.create();
            warningDialog.show();

            todaysWorkout = db.getTodaysWorkout("Advanced Medium Load", 2, 3, 4);
            todaysWorkoutLoaded = true;
        }
        Log.d("TodaysWorkout", "Todays workout=" + todaysWorkout);

        final HashMap<Integer, Integer> setCounts = new HashMap<Integer, Integer>();

        // Lists of the day's exercises per category. These will populate the Spinner dropdowns.
        // Use ArrayList vs. Sets because we don't want to combine exercises if the workout calls
        // For the user to do two squat sessions, etc.
        final ArrayList<String> todaysExercises = new ArrayList<String>();
        ArrayList<String> squatExercises = new ArrayList<String>();
        ArrayList<String> benchExercises = new ArrayList<String>();
        ArrayList<String> deadliftExercises = new ArrayList<String>();

        // Hashset of todays accessories. Will be converted to String array for the accessories
        // Spinner.
        final List<String> exercisesForButtons = new ArrayList<String>();
        HashSet<String> _todaysAccessories = new HashSet<String>();
        HashSet<String> duplicateTester = new HashSet<String>();
        int dayExerciseNumber = -1;

        String Exercise = "";

        for (int i = 0; i < todaysWorkout.size(); i++) {
            WorkoutSet _set = todaysWorkout.get(i);
            Integer frequency = setCounts.get(todaysWorkout.get(i).getDayExerciseNumber());
            setCounts.put(todaysWorkout.get(i).getDayExerciseNumber(), frequency != null ? frequency + 1 : 1);

            // Create a list of today's exercises for the exercise selection buttons to call by
            // index

            while (!Exercise.equals(_set.getExerciseName()) && _set.getExerciseCategory() != 4) {
                Exercise = _set.getExerciseName();
                exercisesForButtons.add(Exercise);
            }

            while (dayExerciseNumber != _set.getDayExerciseNumber()) {
                // Add today's squats to the spinner
                if (_set.getExerciseCategory() == 1) {
                    String exercise = _set.getExerciseName();
                    squatExercises.add(exercise);
                }

                // Add today's benches to the spinner
                if (_set.getExerciseCategory() == 2) {
                    String exercise = _set.getExerciseName();
                    benchExercises.add(exercise);
                }

                // Add today's deadlifts to the spinner
                if (_set.getExerciseCategory() == 3) {
                    String exercise = _set.getExerciseName();
                    deadliftExercises.add(exercise);
                }

                // Add today's accessories to the spinner
                if (_set.getExerciseCategory() == 4) {
                    String exercise = _set.getExerciseName();
                    _todaysAccessories.add(exercise);
                }

                dayExerciseNumber = _set.getDayExerciseNumber();
            }

            // Set buttons inactive by default
            squatSelectButton.setEnabled(true);
            benchSelectButton.setEnabled(true);
            deadliftSelectButton.setEnabled(true);


            // Set the active/inactive workout buttons depending on which workouts are present
            if (todaysWorkout.get(i).getDayExerciseNumber() == 1) {
                squatSelectButton.setEnabled(true);
                if (todaysWorkout.get(i).getExerciseCategory() == 1) {
                    squatSelectButton.setImageResource(R.drawable.squats);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 2) {
                    squatSelectButton.setImageResource(R.drawable.bench_press);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 3) {
                    squatSelectButton.setImageResource(R.drawable.deadlift);
                } else {
                    throw new RuntimeException("Invalid exercise 1");
                }
            }

            if (todaysWorkout.get(i).getDayExerciseNumber() == 2) {
                benchSelectButton.setEnabled(true);

                if (todaysWorkout.get(i).getExerciseCategory() == 1) {
                    benchSelectButton.setImageResource(R.drawable.squats);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 2) {
                    benchSelectButton.setImageResource(R.drawable.bench_press);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 3) {
                    benchSelectButton.setImageResource(R.drawable.deadlift);
                } else {
                    benchSelectButton.setBackgroundResource(R.drawable.dumbell_curls);
                    throw new RuntimeException("Invalid exercise 2");
                }
            }

            if (todaysWorkout.get(i).getDayExerciseNumber() == 3) {
                deadliftSelectButton.setEnabled(true);

                if (todaysWorkout.get(i).getExerciseCategory() == 1) {
                    deadliftSelectButton.setImageResource(R.drawable.squats);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 2) {
                    deadliftSelectButton.setImageResource(R.drawable.bench_press);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 3) {
                    deadliftSelectButton.setImageResource(R.drawable.deadlift);
                    Log.d("DeadliftExercises", "Found=" + deadliftExercises);
                } else {
                    deadliftSelectButton.setBackgroundResource(R.drawable.dumbell_curls);
                    throw new RuntimeException("Invalid exercise 3");
                }
            }

            if (todaysWorkout.get(i).getDayExerciseNumber() == 4 ||
                    todaysWorkout.get(i).getExerciseCategory() == 4) {

                if (todaysWorkout.get(i).getExerciseCategory() == 1) {
                    accessorySpinner.setBackgroundResource(R.drawable.squats);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 2) {
                    accessorySpinner.setBackgroundResource(R.drawable.bench_press);
                } else if (todaysWorkout.get(i).getExerciseCategory() == 3) {
                    accessorySpinner.setBackgroundResource(R.drawable.deadlift);
                } else {
                    accessorySpinner.setBackgroundResource(R.drawable.dumbell_curls);
                    setExerciseButtons(accessorySpinner, new ArrayList<String>(_todaysAccessories));
                }
            }
        }

        Log.i("ButtonValues", "Exercise selection button values = " + exercisesForButtons);

        Log.i("SquatExercises", "original state = " + squatExercises);
        // Test squats for duplicates && add a counter number before exercise if so.
        for (String name : squatExercises) {
            dupeSquats = !duplicateTester.add(name);
        }

        // Test bench for duplicates && add a counter number before exercise if so.
        Log.i("ExerciseList", "Bench sessions: " + benchExercises);
        for (String name : benchExercises) {
            if (!duplicateTester.add(name)) {
                // do nothing
            } else {
                dupeBench = true;
            }
        }

        for (String name : deadliftExercises) {
            if (!duplicateTester.add(name)) {
                // do nothing
            } else {
                dupeDeadlifts = true;
            }
        }

        if (dupeSquats == true) {
            Log.i("ExerciseList", "Creating listed version of squats for spinner");
            // Append counter number
            for (int i = 0; i < squatExercises.size(); i++) {
                String name = squatExercises.get(i);
                String replacementName = String.valueOf(i + 1) + ". " + name;
                squatExercises.set(i, replacementName);
            }
        }

        if (dupeBench) {
            // Append counter number
            for (int i = 0; i < benchExercises.size(); i++) {
                String name = benchExercises.get(i);
                Log.i("ExerciseCounts", "bench name = " + name);
                String replacementName = String.valueOf(i + 1) + ". " + name;
                benchExercises.set(i, replacementName);
            }
        }

        if (dupeDeadlifts) {
            // Append counter number
            for (int i = 0; i < deadliftExercises.size(); i++) {
                String name = deadliftExercises.get(i);
                String replacementName = String.valueOf(i + 1) + ". " + name;
                deadliftExercises.set(i, replacementName);
            }
        }

        Log.i("ExerciseList", "Built squats: " + squatExercises);
        Log.i("ExerciseList", "Built bench: " + benchExercises);
        Log.i("ExerciseList", "Built deadlifts: " + deadliftExercises);
        Log.i("SetCounts", "Set Counts Map = " + setCounts);

        setDisplay.setText("Set " + String.valueOf(currentSetDisplayNumber) + " of " + setCounts.get(currentExerciseNumber));

        Log.i("TodaysWorkout", "Today's workout size = " + todaysWorkout.size());

        currentSet = todaysWorkout.get(setNumber);  // Get first set on load

        // Build map containing sets per lift

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Get rounding value to use for weight display. Default to 5 units.
        roundingValue = Double.valueOf(sharedPref.getString("unitRounding", "5.0"));
        Log.i("SharedPreferences", "Got rounding value " + roundingValue);

        // Set up volume control
        alarmVolumeControl = (CrystalSeekbar) findViewById(R.id.volumeController);
        currentVolume = sharedPref.getInt("alarmVolume", 4);  // Try to get last set volume
        Log.i("SharedPreferences", "Initial volume position: " + currentVolume);
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

        // Try to get the timer duration from shared preferences, defaulting to 1.5 minutes if it
        // hasn't been set. Time is stored in milliseconds in sharedPreferences. The time is saved
        // in the sharedPreferences file in RestDurationPicker.
        timerDurationSeconds = (int) (long) (sharedPref.getLong("timerDuration", 90000) / 1000);

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

        // Get users maxes for weight calculation
        UserMaxEntry userMaxEntry = db.getLastUserMaxEntry();

        String [] deadliftWorkout = new String[]{"Deadlift", "1 + 1/2 (X2) Deadlift"};
        String [] benchWorkout = new String[]{"Bench Press", "Close Grip Bench Press"};

        try {
            //readFromUserParamDb();  // Load databases

            squat_max = userMaxEntry.getSquatMax();
            bench_max = userMaxEntry.getBenchMax();
            deadlift_max = userMaxEntry.getDeadliftMax();

        } catch (NullPointerException e) {
            Log.d("DbReadError", "User parameter DB read error: " + e);  // First creation of database.
        }

        //Double currentWeight = new Double(currentSet.getWeightPercentage() * 100);
        currentWeight = setCurrentWeight();
        Integer reps = currentSet.getReps();
        current_exercise_string = currentSet.getExerciseName();

        repPicker.setValue(reps);
        weightPicker.setValue((currentWeight.intValue() - 1) / 5);
        currentExercise.setText(current_exercise_string);

        /* TODO: Change the exercise buttons depending on what text is used for the exercise that
           TODO: day. For example: on day 1, you do front squats instead of regular squats. Front squats and
           TODO: squats should both appear under the "squats" button.
         */

        // Listen for changes in rep numberPicker
        repPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (viewingPastSet) {
                    repsChanged = true;
                }
            }
        });

        weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (viewingPastSet) {
                    weightChanged = true;
                }
            }
        });

        // TODO: Exercise selection button code here

        // Run the nextSet button alongside the autotimer listener.
        this.nextSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the save all sets button
                saveAllSets.setVisibility(View.VISIBLE);
                editor.putBoolean("inAWorkout", true);
                editor.commit();

                if (setNumber == moveBetweenSetsCounter && setNumber < todaysWorkout.size()) {  // If user is at current set
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
                    String workoutId = String.valueOf(db.getWorkoutHistoryRowCount() + 1);
                    int currentReps = repPicker.getValue();
                    //Double currentWeight = Double.valueOf((weightPicker.getValue() + 1) * 5);
                    currentWeight = setCurrentWeight();
                    Log.i("SetSaved", "Current reps: " + currentReps + ", " +
                            "current weight: " + currentWeight);

                    currentSet = todaysWorkout.get(moveBetweenSetsCounter);
                    //currentWeight = new Double(currentSet.getWeightPercentage() * 100);
                    current_exercise_string = currentSet.getExerciseName();

                    if (currentSet.getDayExerciseNumber() != currentExerciseNumber) {
                        newExercise = true;
                        currentExerciseNumber = currentSet.getDayExerciseNumber();
                        Log.i("NewExercise", "moved to next exercise");
                    } else {
                        newExercise = false;
                        Log.i("NewExercise", "Still doing " + current_exercise_string);
                    }

                    if (newExercise && currentSet.getExerciseCategory() == 4) {
                        repPicker.setValue(currentReps);
                        weightPicker.setValue((currentWeight.intValue() - 1) / 5);
                    } else if (currentSet.getExerciseCategory() != 4) {
                        repPicker.setValue(currentReps);
                        weightPicker.setValue((currentWeight.intValue() - 1) / 5);
                    }

                    if (setNumber < todaysWorkout.size()) {
                        db.addWorkoutHistory(new WorkoutHistory(workoutId, date,
                                current_exercise_string, currentSetDisplayNumber, currentReps,
                                currentWeight, currentProgram, currentCycle, currentWeek,
                                currentDay, currentExerciseNumber, 0));

                        Log.i("ExerciseCategory", "Exercise category=" + currentSet.getExerciseCategory());

                        workoutHistoryRow = db.getWorkoutHistoryRowCount();
                        Log.d("Database", "Committed workout history to database. There are now " + workoutHistoryRow + " rows.");
                        Log.d("CurrentWorkout", setNumber + " out of " + (todaysWorkout.size()) + " sets");
                    }

                    setNumber += 1;
                    moveBetweenSetsCounter = setNumber;
                    Log.i("SetNumber", "New set number = " + setNumber);

                    editor.putInt("lastSetNumberCompleted", setNumber);
                    editor.commit();

                    // Get the next set if we aren't on the last set of the workout
                    if (moveBetweenSetsCounter < todaysWorkout.size() -1) {

                        // Update the set display counter on a new exercise for the day
                        if (todaysWorkout.get(moveBetweenSetsCounter).getDayExerciseNumber() !=
                                currentExerciseNumber) {
                            currentSetDisplayNumber = 0;
                        }

                        if (moveBetweenSetsCounter == todaysWorkout.size()) {
                            // Set nextSet button disabled
                            nextSetButton.setEnabled(false);
                        }

                        Log.i("NewSetNumber", "New set number=" + moveBetweenSetsCounter);

                        currentSet = todaysWorkout.get(moveBetweenSetsCounter);
                        currentReps = currentSet.getReps();
                        currentSetDisplayNumber += 1;

                        // Display set number + 1, since setNumber begins at 0
                        setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " + setCounts.get(currentSet.getDayExerciseNumber()));

                        // TODO: Get users maxes for weight calculation
                        //currentWeight = new Double(currentSet.getWeightPercentage() * 100);
                        currentWeight = setCurrentWeight();
                        current_exercise_string = currentSet.getExerciseName();
                        currentExercise.setText(current_exercise_string);

                        // TODO: Fix bug where accessory weight is reset each time next set is pressed
                        if (newExercise && currentSet.getExerciseCategory() == 4) {
                            repPicker.setValue(currentReps);
                            weightPicker.setValue((currentWeight.intValue() - 1) / 5);
                            Log.i("NextSet", "Getting new reps & weights");
                        } else if (currentSet.getExerciseCategory() != 4) {
                            repPicker.setValue(currentReps);
                            weightPicker.setValue((currentWeight.intValue() - 1) / 5);
                            Log.i("NextSet", "Getting new reps & weights");
                        }

                        Log.d("NextSet", "Next set: " + currentSet);

                        // Enable the previous set button if it is disabled
                        if (!previousSetButton.isEnabled()) {
                            previousSetButton.setEnabled(true);
                            Log.d("DisabledButton", "Previous Set button enabled");
                        }
                    } else {  // The end of the workout session.
                        Log.i("EndOfWorkout", "End of workout reached");
                        setDisplay.setText("Set " + (currentSetDisplayNumber + 1) + " of " + setCounts.get(currentSet.getDayExerciseNumber()));
                    }

                } else if (setNumber > moveBetweenSetsCounter + 1 && moveBetweenSetsCounter < todaysWorkout.size() - 1) { // Go forward in history

                    // First, get values for current set in view to check if they've been changed
                    Log.i("NextSetInHistory", "SetNumber=" + setNumber + ", moveBetweenSetsCounter=" + moveBetweenSetsCounter);
                    final int currentDbRow = (workoutHistoryRow - (setNumber - moveBetweenSetsCounter));
                    Log.i("NextSetInHistory", "Moving to row " + currentDbRow);
                    WorkoutHistory nextSet = db.getWorkoutHistoryAtId(currentDbRow);
                    int reps = nextSet.getReps();
                    int weight = nextSet.getWeight().intValue();  // TODO: Fix this. It displays incorrect weight because it doesn't update;
                    current_exercise_string = nextSet.getExercise();

                    // Do current rep and weight picker values match what's in the table?
                    Log.i("Forward", "repPicker value=" + repPicker.getValue() + " db reps value=" + reps +
                            " weightPicker value=" + (weightPicker.getValue() + 1) * 5 + " db weight value=" + weight);

                    if (repsChanged || weightChanged) {

                        // update the table
                        int new_reps = repPicker.getValue();
                        Double new_weight = Double.valueOf((weightPicker.getValue() + 1) * 5);

                        final WorkoutHistory changedWorkoutHistory = new WorkoutHistory("0", date,
                                current_exercise_string, currentSetDisplayNumber, new_reps,
                                new_weight, currentProgram, currentCycle, currentWeek, currentDay,
                                nextSet.getExerciseNumber(), 0);

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
                        repsChanged = false;
                        weightChanged = false;
                        changeSetPrompt.show();
                    }

                    // Now, get the values for the next set to view

                    moveBetweenSetsCounter += 1;
                    Log.i("NextSetInHistory", "SetNumber=" + setNumber + ", moveBetweenSetsCounter=" + moveBetweenSetsCounter);

                    nextSet = db.getWorkoutHistoryAtId((workoutHistoryRow + 1 - (setNumber - moveBetweenSetsCounter)));
                    reps = nextSet.getReps();
                    weight = nextSet.getWeight().intValue();
                    current_exercise_string = nextSet.getExercise();

                    repPicker.setValue(reps);
                    weightPicker.setValue((weight - 1) / 5);
                    currentSetDisplayNumber += 1;
                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " + setCounts.get(currentSet.getDayExerciseNumber()));
                    currentExercise.setText(current_exercise_string);

                    Log.d("NextSetInHistory", "Set Data=" + nextSet);

                    if (moveBetweenSetsCounter == todaysWorkout.size() - 1) {
                        // Set nextSet button disabled
                        nextSetButton.setEnabled(false);
                        Log.d("DisabledButton", "Next Set button disabled");
                    }

                    // Enable the previous set button if it is disabled
                    if (!previousSetButton.isEnabled()) {
                        previousSetButton.setEnabled(true);
                        Log.d("DisabledButton", "Previous Set button enabled");
                    }
                }

                else if (setNumber - 1 == moveBetweenSetsCounter) {  // This runs on the transition from old sets back to the current set
                    moveBetweenSetsCounter = setNumber;
                    Log.i("BackAtCurrentSet", "Set number=" + moveBetweenSetsCounter);

                    // Enable the previous set button if it is disabled
                    if (!previousSetButton.isEnabled()) {
                        previousSetButton.setEnabled(true);
                        Log.d("DisabledButton", "Previous Set button Enabled");
                    }

                    // Display set number + 1, since setNumber begins at 0
                    currentSetDisplayNumber += 1;
                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " + setCounts.get(currentSet.getDayExerciseNumber()));

                    currentSet = todaysWorkout.get(moveBetweenSetsCounter);
                    int currentReps = currentSet.getReps();

                    // TODO: Get users maxes for weight calculation
                    Double weight = setCurrentWeight();
                    current_exercise_string = currentSet.getExerciseName();

                    repPicker.setValue(currentReps);
                    weightPicker.setValue((weight.intValue() - 1) / 5);
                    currentExercise.setText(current_exercise_string);

                    Log.d("BackAtCurrentSet", "Next set: " + currentSet);
                }
            }
        });

        this.previousSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement set-change code here too.
                // Go back to last-entered set if it exists
                if (moveBetweenSetsCounter >= 1 && moveBetweenSetsCounter <= setNumber) {

                    moveBetweenSetsCounter -= 1;
                    int currentDbRow = (workoutHistoryRow + 1) - (setNumber - moveBetweenSetsCounter);
                    WorkoutHistory lastSet = db.getWorkoutHistoryAtId(currentDbRow);

                    if (!viewingPastSet) {
                        viewingPastSet = true;
                        currentSetDisplayNumber = setCounts.get(lastSet.getExerciseNumber());
                    } else if (currentSetDisplayNumber > 1) {
                        currentSetDisplayNumber -= 1;
                    }

                    if (moveBetweenSetsCounter == 0) {
                        // Set nextSet button disabled
                        previousSetButton.setEnabled(false);
                        Log.d("DisabledButton", "Previous Set button disabled");
                    }

                    // Enable the previous set button if it is disabled
                    if (!nextSetButton.isEnabled()) {
                        nextSetButton.setEnabled(true);
                        Log.d("DisabledButton", "Next Set button enabled");
                    }

                    Log.i("MoveToOldSet", "Set number: " + setNumber + " moveBetweenSetsCounter=" + moveBetweenSetsCounter);
                    // Get previous workout history row
                    Log.i("MoveBetweenSets", "Getting row number " + currentDbRow + " of" + workoutHistoryRow);
                    Log.i("WorkoutHistory", "Set on set " + currentDbRow + ":" + lastSet.toString());
                    int reps = lastSet.getReps();
                    int weight = lastSet.getWeight().intValue();
                    repPicker.setValue(reps);
                    weightPicker.setValue((weight - 1) / 5);
                    current_exercise_string = lastSet.getExercise();

                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " + setCounts.get(lastSet.getExerciseNumber()));
                    currentExercise.setText(current_exercise_string);

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
                startActivity(new Intent(TrainActivity.this, selectProgramActivity.class));
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

        saveAllSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SaveAllSets", "Saving all sets on " + date);

                // The following needs to return a list of all workouts matching date. Then, for
                // each id in the list, change the "persist" column to 1.
                List<WorkoutHistory> allSetsOnDate = db.getWorkoutHistoryAtDate(date);
                Log.i("SaveAllSets", "Got workout history " + allSetsOnDate);
                Toast.makeText(getApplicationContext(), "Saved workout", Toast.LENGTH_LONG).show();

                for (int i = 0; i < allSetsOnDate.size(); i++) {
                    if (allSetsOnDate.get(i).getPersist() == 0) {
                        WorkoutHistory persistedWorkoutHistory = allSetsOnDate.get(i);
                        persistedWorkoutHistory.setPersist(1);
                        Integer id = Integer.valueOf(persistedWorkoutHistory.getWorkoutId());

                        try {
                            db.changeWorkoutHistoryAtId(id, persistedWorkoutHistory);
                            inAWorkout = false;
                            editor.putBoolean("inAWorkout", inAWorkout);
                            editor.commit();
                            Log.i("SaveAllSets", "Found unsaved set " + allSetsOnDate.get(i));
                        } catch (Exception e) {
                            Log.e("SaveAllSets", "Error saving sets: " + e);
                            FirebaseCrash.report(new Exception("Could not save all sets. " +
                                    "Threw error" + e));
                        }
                    }
                }
            }
        });

        squatSelectButton.setOnClickListener(new View.OnClickListener() {
            private int firstExerciseFirstSetNumber;
            private boolean foundExerciseString = false;  // Set to true when first set is found

            @Override
            public void onClick(View v) {
                // TODO: write code to change to exercise in excerciseSelectionButton 1
                try {
                    current_exercise_string = exercisesForButtons.get(0);  // Get string
                    Log.i("ExcerciseSelection", "User selected " + current_exercise_string);
                    currentExercise.setText(current_exercise_string);
                    currentSetDisplayNumber = 1;

                    // find first setnumber of this exercise
                    for (int i = 0; i < todaysWorkout.size(); i++) {
                        if (!foundExerciseString) {
                            if (todaysWorkout.get(i).getExerciseName().equals(current_exercise_string)) {
                                foundExerciseString = true;
                                firstExerciseFirstSetNumber = todaysWorkout.get(i).get_id();
                                Log.i("ExerciseSelection", "Got exercise "
                                        + current_exercise_string + " at set " +
                                        firstExerciseFirstSetNumber);
                            }
                        }
                    }

                    // TODO: Get current set at index=firstExerciseFirstSetNumber
                    currentSet = todaysWorkout.get(firstExerciseFirstSetNumber);
                    Log.i("ExerciseSelection", "Got " + current_exercise_string + " set " +
                            currentSet);
                    Log.d("ExerciseSelection", "The size of the todaysWorkout list is " + todaysWorkout.size());

                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " +
                            setCounts.get(1));
                } catch (IndexOutOfBoundsException e) {
                    // Somehow user selected a button that was inactive??
                    Log.e("ExerciseSelection", "User selected exercise button 1, " +
                            "which was out of bounds. The error was '" + e + ".'");
                }
            }
        });

        benchSelectButton.setOnClickListener(new View.OnClickListener() {
            private boolean foundExerciseString = false;
            private int secondExerciseFirstSet;

            @Override
            public void onClick(View v) {
                // TODO: write code to change to exercise in excerciseSelectionButton 1
                try {
                    current_exercise_string = exercisesForButtons.get(1);
                    Log.i("ExcerciseSelection", "User selected " + current_exercise_string);
                    currentExercise.setText(current_exercise_string);
                    currentSetDisplayNumber = 1;

                    // find first setnumber of this exercise
                    for (int i = 0; i < todaysWorkout.size(); i++) {
                        if (!foundExerciseString) {
                            if (todaysWorkout.get(i).getExerciseName().equals(current_exercise_string)) {
                                foundExerciseString = true;
                                secondExerciseFirstSet = todaysWorkout.get(i).get_id();
                                Log.i("ExerciseSelection", "Got exercise "
                                        + current_exercise_string + " at set " +
                                        secondExerciseFirstSet);
                            }
                        }
                    }

                    // TODO: Get current set at index=firstExerciseFirstSetNumber
                    currentSet = todaysWorkout.get(secondExerciseFirstSet);
                    Log.i("ExerciseSelection", "Got " + current_exercise_string + " set " +
                            currentSet);
                    Log.d("ExerciseSelection", "The size of the todaysWorkout list is " + todaysWorkout.size());
                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " +
                            setCounts.get(2));

                } catch (IndexOutOfBoundsException e) {
                    // Somehow user selected a button that was inactive??
                    Log.e("ExerciseSelection", "User selected exercise button 2, which was out of bounds");
                }
            }
        });

        deadliftSelectButton.setOnClickListener(new View.OnClickListener() {
            private boolean foundExerciseString = false;
            private int thirdExerciseFirstSet;

            @Override
            public void onClick(View v) {
                // TODO: write code to change to exercise in excerciseSelectionButton 1
                try {
                    current_exercise_string = exercisesForButtons.get(2);
                    Log.i("ExcerciseSelection", "User selected " + current_exercise_string);
                    currentExercise.setText(current_exercise_string);
                    currentSetDisplayNumber = 1;
                    currentSetDisplayNumber = 1;
                    setDisplay.setText(String.valueOf(currentSetDisplayNumber));

                    // find first setnumber of this exercise
                    for (int i = 0; i < todaysWorkout.size(); i++) {
                        if (!foundExerciseString) {
                            if (todaysWorkout.get(i).getExerciseName().equals(current_exercise_string)) {
                                foundExerciseString = true;
                                thirdExerciseFirstSet = todaysWorkout.get(i).get_id();
                                Log.i("ExerciseSelection", "Got exercise "
                                        + current_exercise_string + " at set " +
                                        thirdExerciseFirstSet);
                            }
                        }
                    }

                    // TODO: Get current set at index=firstExerciseFirstSetNumber
                    currentSet = todaysWorkout.get(thirdExerciseFirstSet);
                    Log.i("ExerciseSelection", "Got " + current_exercise_string + " set " +
                            currentSet);
                    Log.d("ExerciseSelection", "The size of the todaysWorkout list is " + todaysWorkout.size());

                    // Get first set for this exercise
                    Log.i("ExerciseSelection", "Got set " + todaysWorkout);

                    setDisplay.setText("Set " + (currentSetDisplayNumber) + " of " +
                            setCounts.get(3));
                } catch (IndexOutOfBoundsException e) {
                    // Somehow user selected a button that was inactive??
                    Log.e("ExerciseSelection", "User selected exercise button 3, which was out of bounds");
                }
            }
        });

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
        Double step = roundingValue;

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

    private Double roundDouble(Double i, Double v) {
        Double value = Double.valueOf((Math.round(i / v) * v));
        Log.i("RoundDouble", "Rounded " + i + " by " + v + " to " + value);
        return value;
    }

    private Double setCurrentWeight() {
        // Set weight according to lift type. Round the weight to whichever value is specified in
        // SettingsActivity
        if (currentSet.getExerciseCategory() == 1) {
            if (squat_max != null) {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * squat_max), roundingValue);
            } else {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * 100), roundingValue);
            }

        } else if (currentSet.getExerciseCategory() == 2) {
            if (bench_max != null) {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * bench_max), roundingValue);
            } else {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * 100), roundingValue);
            }
        } else if (currentSet.getExerciseCategory() == 3) {
            if (bench_max != null) {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * deadlift_max), roundingValue);
            } else {
                currentWeight = roundDouble(new Double(currentSet.getWeightPercentage() * 100), roundingValue);
            }
        } else if (currentSet.getExerciseCategory() == 4) {
            currentWeight = new Double(135);
        }

        else {  // Accessory weight - default to 135
            currentWeight = new Double(135);
            Log.e("WorkoutCat!", "Forgot workout cat" + currentSet);
        }

        return currentWeight;
    }

    private void setExerciseButtons(Spinner exerciseSpinner, ArrayList<String> exercises) {
        // TODO: Put the lists in the correct Spinners. Since they change dynamically, this
        // will have to as well.

        // Convert _todaysAccessories ArrayList to String Array todaysAccessories for the Spinner.
        String[] todaysAccessories = exercises.toArray(new String[exercises.size()]);
        CustomAdapter<String> accessorySpinnerAdapter = new CustomAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, todaysAccessories);
        Log.i("AddExercise", "Adding accessories" + exercises);
        exerciseSpinner.setAdapter(accessorySpinnerAdapter);
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