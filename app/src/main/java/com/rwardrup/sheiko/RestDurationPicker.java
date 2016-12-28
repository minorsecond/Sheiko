package com.rwardrup.sheiko;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;

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
    }

    public interface DurationListener {
        void onDurationSet(long duration);
    }
}