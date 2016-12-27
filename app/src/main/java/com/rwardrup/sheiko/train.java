package com.rwardrup.sheiko;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class train extends AppCompatActivity {
    public Integer customTimerlength = null;
    public Integer timerDurationSeconds = 180;  // 3 minutes is a good default value
    public boolean timerIsPaused;
    public long millisLeftOnTimer;
    Button startBreakTimerButton;
    Button stopBreakTimerButton;
    Button pauseBreakTimerButton;
    TextView breakTimerOutput;
    CountDownTimer countdowntimer;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        startBreakTimerButton = (Button) findViewById(R.id.startBreakTimer);
        stopBreakTimerButton = (Button) findViewById(R.id.stopBreakButton);
        pauseBreakTimerButton = (Button) findViewById(R.id.pauseBreakButton);
        breakTimerOutput = (TextView) findViewById(R.id.breakTimerOutput);

        // Break timer long-click set time
        breakTimerOutput.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                customTimerlength = timerLengthInputAlert();

                // Set the timer duration in seconds
                if (customTimerlength != null) {
                    timerDurationSeconds = customTimerlength;
                } else {
                    timerDurationSeconds = 10;
                }

                // Assign the new custom timer duration to the timerduration variable
                breakTimerOutput.setText(Integer.toString(timerDurationSeconds));
                Log.d("NewTimer", "New Timer Duration: " + timerDurationSeconds);
                return true;
            }
        });


        breakTimerOutput.setText(Integer.toString(timerDurationSeconds));

        // break timer start
        startBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (timerIsPaused) {
                    mCountDownTimer = createTimer(millisLeftOnTimer);  // resume paused timer

                    Log.d("resumed timer", "value: " + millisLeftOnTimer);
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
                    timerIsPaused = false;
                    mCountDownTimer.cancel();
                    breakTimerOutput.setTextSize(36);
                    breakTimerOutput.setText(Integer.toString(timerDurationSeconds));
                }
            }
        });

        // break timer pause
        pauseBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timerIsPaused = true;
                mCountDownTimer.cancel();
                Log.d("millis left on pause", "value: " + millisLeftOnTimer);
            }
        });
    }

    // TODO: Use minutes and seconds format, and do a count up vs. count down.
    private CountDownTimer createTimer(long timerDuration) {

        Log.d("new timer duration:", "value: " + timerDuration);
        return new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 1000);
                breakTimerOutput.setText(Integer.toString(progress));
                millisLeftOnTimer = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                breakTimerOutput.setTextSize(24);
                breakTimerOutput.setText(" Break Over");
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

    // Timer length input alert
    public Integer timerLengthInputAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Timer Length");

        // Set up the input
        final EditText input = new EditText(this);
        // We expect an integer (n seconds)
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        builder.setView(input);

        // Set up buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customTimerlength = Integer.parseInt(input.getText().toString());  // Get the value
            }
        });

        // cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        return customTimerlength;
    }
}