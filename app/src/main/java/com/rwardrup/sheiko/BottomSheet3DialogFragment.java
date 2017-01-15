package com.rwardrup.sheiko;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

// Handle the bottom timer drawer
public class BottomSheet3DialogFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_timer_bottom_sheet, null);
        dialog.setContentView(contentView);
    }
}