package com.rwardrup.sheiko;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

// TODO: get timer duration from TrainActivity and pass it to CountDownTimer

public class BreakTimer extends Service {
    public static final String COUNTDOWN_BR = "com.rwardrup.sheiko.countdown_br";
    private final static String TAG = "BroadcastService";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    private int timerDuration;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("TimerDurationSet", "Timer duration is set to " + timerDuration);

        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(90000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                playAlertSound();
            }
        };

        cdt.start();
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

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}