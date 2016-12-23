package com.rwardrup.sheiko;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class train extends AppCompatActivity {

    Button button;
    TextView textview;
    CountDownTimer countdowntimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        button = (Button) findViewById(R.id.startBreakTimer);
        textview = (TextView) findViewById(R.id.breakTimerOutput);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                countdowntimer = new CountDownTimerClass(180000, 1000);

                countdowntimer.start();

            }
        });
    }


    public class CountDownTimerClass extends CountDownTimer {

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);

            textview.setText(Integer.toString(progress));

        }

        @Override
        public void onFinish() {
            textview.setTextSize(18);
            textview.setText(" Break Over");

        }
    }


}