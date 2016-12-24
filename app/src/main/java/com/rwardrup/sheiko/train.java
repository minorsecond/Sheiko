package com.rwardrup.sheiko;

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

        // break timer start
        startBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (timerIsPaused) {
                    mCountDownTimer = createTimer(millisLeftOnTimer);  // resume paused timer

                    Log.d("resumed timer", "value: " + millisLeftOnTimer);
                } else {
                    breakTimerOutput.setTextSize(31);
                    int timerDuration = 180000;  // This will be set by user in final code
                    mCountDownTimer = createTimer(timerDuration);
                }
            }
        });

        // break timer stop
        stopBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timerIsPaused = false;
                mCountDownTimer.cancel();
                breakTimerOutput.setTextSize(31);
                breakTimerOutput.setText("180");
            }
        });

        // break timer pause
        pauseBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timerIsPaused = true;
                mCountDownTimer.cancel();  // TODO: Fix this
                Log.d("millis left on pause", "value: " + millisLeftOnTimer);
            }
        });
    }

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
            }
        }.start();
    }

}