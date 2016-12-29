package com.rwardrup.sheiko;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class train extends AppCompatActivity implements RestDurationPicker.DurationListener {
    private static long millisLeftOnTimer;
    Button startBreakTimerButton;
    Button stopBreakTimerButton;
    Button pauseBreakTimerButton;

    // Activity buttons
    Button squatSelectButton;
    Button benchSelectButton;
    Button deadliftSelectButton;
    Button accessorySelectButton;

    // Text output
    TextView breakTimerOutput;
    TextView currentExercise;

    // Timer stuff
    private Integer timerDurationSeconds = 180;  // 3 minutes is a good default value
    private boolean timerIsPaused;
    private CountDownTimer mCountDownTimer;

    // Set font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        startBreakTimerButton = (Button) findViewById(R.id.startBreakTimer);
        stopBreakTimerButton = (Button) findViewById(R.id.stopBreakButton);
        pauseBreakTimerButton = (Button) findViewById(R.id.pauseBreakButton);
        squatSelectButton = (Button) findViewById(R.id.squatSelectButton);
        benchSelectButton = (Button) findViewById(R.id.benchSelectButton);
        deadliftSelectButton = (Button) findViewById(R.id.deadliftButton);
        accessorySelectButton = (Button) findViewById(R.id.accessoriesButton);

        breakTimerOutput = (TextView) findViewById(R.id.breakTimerOutput);
        currentExercise = (TextView) findViewById(R.id.currentExerciseDisplay);

        // Set the reps and weights
        setRepsWeightPickers();

        this.squatSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText("Squat");
            }
        });

        this.benchSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText("Bench");
            }
        });

        this.deadliftSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText("Deadlift");
            }
        });

        this.accessorySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentExercise.setText("Accessories");
            }
        });

        // Handle user long-clicking on the timer output text to change timer length on-the-fly
        // This utilizes the onDurationSet method at the bottom of this class.
        breakTimerOutput.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new RestDurationPicker().show(getFragmentManager(), "Session break length");
                return true;
            }
        });
        breakTimerOutput.setText(secondsToString(timerDurationSeconds));

        // break timer start
        startBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (timerIsPaused) {
                    mCountDownTimer = createTimer(millisLeftOnTimer);  // resume paused timer

                    Log.d("resumed timer", "value: " + millisLeftOnTimer);
                } else if (millisLeftOnTimer > 0) {  // Do nothing if there is still time left
                } else {
                    breakTimerOutput.setTextSize(36);
                    int timerDuration = timerDurationSeconds * 1000;  // This will be set by user in final code
                    mCountDownTimer = createTimer(timerDuration);
                }
            }
        });

        // break timer stop
        stopBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (millisLeftOnTimer > 0) {
                    millisLeftOnTimer = 0;
                    timerIsPaused = false;
                    mCountDownTimer.cancel();
                    breakTimerOutput.setTextSize(36);
                    breakTimerOutput.setText(secondsToString(timerDurationSeconds));
                }
            }
        });

        // break timer pause
        pauseBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (millisLeftOnTimer > 0) {
                    timerIsPaused = true;
                    mCountDownTimer.cancel();
                    Log.d("millis left on pause", "value: " + millisLeftOnTimer);
                }
            }
        });
    }

    // TODO: Use minutes and seconds format, and do a count up vs. count down.
    public CountDownTimer createTimer(long timerDuration) {

        Log.d("new timer duration:", "value: " + timerDuration);
        return new CountDownTimer(timerDuration, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 1000);
                breakTimerOutput.setText(secondsToString(progress));
            }

            @Override
            public void onFinish() {
                breakTimerOutput.setText(secondsToString(timerDurationSeconds));
                playAlertSound();  // TODO: Fix the delay before playing beep.
            }
        }.start();
    }

    public void playAlertSound() {
        // Plays sound when timer reaches end.

        MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.timer_end_beep);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        // timer alert looping and volume
        mp.setLooping(false);
        mp.setVolume(1.0f, 1.0f);
        mp.start();
    }

    // Break timer long-click set time
    @Override
    public void onDurationSet(long duration) {
        Integer i = (int) (long) duration;  // get integer i from duration (long)
        timerDurationSeconds = i / 1000; // convert millis to seconds

        // Set the timer duration in seconds

        // Assign the new custom timer duration to the timerduration variable
        breakTimerOutput.setText(secondsToString(timerDurationSeconds));
        Log.d("NewTimer", "New Timer Duration: " + secondsToString(timerDurationSeconds));
    }

    // convert seconds to minutes and seconds for display
    private String secondsToString(int pTime) {
        return String.format(Locale.US, "%02d:%02d", pTime / 60, pTime % 60);
    }

    // set reps and weight picker contents
    private void setRepsWeightPickers() {

        NumberPicker repPicker = (NumberPicker) findViewById(R.id.repsPicker);
        NumberPicker weightPicker = (NumberPicker) findViewById(R.id.weightPicker);

        // Disable keyboard
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
}