package com.rwardrup.sheiko;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;
// from https://github.com/svenwiegand/time-duration-picker

public class RestDurationPicker extends TimeDurationPickerDialogFragment {

    @Override
    protected long getInitialDuration() {
        return 0;  // Default to empty
    }

    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.MM_SS;
    }

    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        //When the duration is set by the user, notify the listener
        ((train) getActivity()).onDurationSet(duration);

        // Shared prefs
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong("timerDuration", duration);
        Log.d("TimerSettings", "Timer duration set: " + duration);
        editor.apply();
    }

    public interface DurationListener {
        void onDurationSet(long duration);
    }
}