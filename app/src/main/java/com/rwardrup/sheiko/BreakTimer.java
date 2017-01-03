package com.rwardrup.sheiko;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;


// TODO: get timer duration from TrainActivity and pass it to CountDownTimer

public class BreakTimer extends Service {
    public static final String COUNTDOWN_BR = "com.rwardrup.sheiko.countdown_br";
    private final static String TAG = "BroadcastService";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Get alarm duration. Defaults to 1.5 minutes
        final long timerDuration = sharedPref.getLong("timerDuration", 90000);

        // Get alarm volume
        final int currentVolume = sharedPref.getInt("alarmVolume", 5);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        Log.i("TimerVolume", "Current timer volume: " + currentVolume);

        Log.d("TimerDurationSet", "Timer duration is set to " + timerDuration);

        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(timerDuration, 1000) {

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
                bi.putExtra("countdown", timerDuration);  // Reset timer display
                sendBroadcast(bi);
                stopSelf();
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
        //mp.setVolume(1.0f, 1.0f);
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