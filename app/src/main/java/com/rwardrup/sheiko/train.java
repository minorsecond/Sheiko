package com.rwardrup.sheiko;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class train extends AppCompatActivity {

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

        // Set the timer duration in seconds
        final int timerDurationSeconds = 10;
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
}