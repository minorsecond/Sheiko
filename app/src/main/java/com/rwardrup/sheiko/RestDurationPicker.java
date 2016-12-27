package com.rwardrup.sheiko;

import android.widget.Toast;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;


public class RestDurationPicker extends TimeDurationPickerDialogFragment {

    @Override
    protected long getInitialDuration() {
        return 3 * 60 * 1000;  // Default to 3 minutes
    }

    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.MM_SS;
    }

    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        Toast.makeText(getContext(), "New break length set", Toast.LENGTH_LONG).show();
    }
}