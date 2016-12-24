package com.rwardrup.sheiko;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class train extends AppCompatActivity {

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
                breakTimerOutput.setTextSize(31);
                //countdowntimer = new CountDownTimerClass(180000, 1000);
                //countdowntimer.start();

                mCountDownTimer = createTimer();
            }
        });

        // break timer stop
        stopBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCountDownTimer.cancel();
                breakTimerOutput.setTextSize(31);
                breakTimerOutput.setText("180");
                //countdowntimer = new CountDownTimerClass(180000, 1000);
            }
        });

        // break timer pause
        pauseBreakTimerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                countdowntimer.cancel();  // TODO: Fix this
            }
        });
    }

    private CountDownTimer createTimer() {
        return new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 1000);
                breakTimerOutput.setText(Integer.toString(progress));
            }

            @Override
            public void onFinish() {
                breakTimerOutput.setTextSize(24);
                breakTimerOutput.setText(" Break Over");
            }
        }.start();
    }

}