package com.torch.duration_picker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.math.MathUtils;

public class DurationPickerDialog extends AlertDialog implements DialogInterface.OnClickListener,
        DurationPicker.OnDurationChangedListener {

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    private final DurationPicker mDurationPicker;
    private final int mInitialHourOfDay;
    private final int mInitialMinute;
    private final OnDurationSetListener mDurationSetListener;

    @Override
    public void onDurationChanged(DurationPicker view, int hourOfDay, int minute) {
        /* Does Nothing */
    }

    /**
     * The callback interface used to indicate the user is done filling in
     * the duration (e.g. they clicked on the 'OK' button).
     */
    public interface OnDurationSetListener {
        /**
         * Called when the user is done setting a new time and the dialog has
         * closed.
         *
         * @param view   the view associated with this listener
         * @param hour   the hour that was set
         * @param minute the minute that was set
         */
        void onDurationSet(DurationPicker view, int hour, int minute);
    }

    public DurationPickerDialog(Context context, OnDurationSetListener listener, int hourOfDay, int minute) {
        this(context, 0, listener, hourOfDay, minute);
    }

    public DurationPickerDialog(Context context, int themeResId, OnDurationSetListener listener, int hourOfDay, int minute) {
        super(context, resolveDialogTheme(context, themeResId));

        mDurationSetListener = listener;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.duration_picker_dialog, null);
        setView(view);
        setTitle("Set Duration");
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.set), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this);

        mDurationPicker = view.findViewById(R.id.durationPicker);
        setHour(mInitialHourOfDay);
        setMinute(mInitialMinute);
        mDurationPicker.setDurationChangedListener(this);
    }

    private void setHour(int hour) {
        mDurationPicker.setHour(MathUtils.clamp(hour, 0, 23));
    }

    private void setMinute(int minute) {
        mDurationPicker.setMinute(MathUtils.clamp(minute, 0, 59));
    }


    public DurationPicker getDurationPicker() {
        return mDurationPicker;
    }

    @Override
    public void show() {
        super.show();
        getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DurationPickerDialog.this.onClick(DurationPickerDialog.this, BUTTON_POSITIVE);
                // Clearing focus forces the dialog to commit any pending
                // changes, e.g. typed text in a NumberPicker.
                mDurationPicker.clearFocus();
                dismiss();
            }
        });
    }


    static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.durationPickerTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return resId;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                // Note this skips input validation and just uses the last valid time and hour
                // entry. This will only be invoked programmatically. User clicks on BUTTON_POSITIVE
                // are handled in show().
                if (mDurationSetListener != null) {
                    mDurationSetListener.onDurationSet(mDurationPicker, mDurationPicker.getHour(),
                            mDurationPicker.getMinute());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }


    /**
     * Sets the current duration.
     */
    public void updateDuration(int hour, int minuteOfHour) {
        setHour(hour);
        setMinute(minuteOfHour);
    }


    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mDurationPicker.getHour());
        state.putInt(MINUTE, mDurationPicker.getMinute());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int hour = savedInstanceState.getInt(HOUR);
        final int minute = savedInstanceState.getInt(MINUTE);
        setHour(hour);
        setMinute(minute);
    }
}
