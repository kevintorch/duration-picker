package com.torch.duration_picker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.InspectableProperty;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Locale;

public class DurationPicker extends FrameLayout {
    private static final String LOG_TAG = DurationPicker.class.getSimpleName();

    private final NumberPicker mHourSpinner;
    private final EditText mHourSpinnerInput;
    private final TextView mDivider;
    private final NumberPicker mMinuteSpinner;
    private final EditText mMinuteSpinnerInput;
    private final Calendar mTempCalendar;


    private OnDurationChangedListener durationChangedListener;

    public void setDurationChangedListener(OnDurationChangedListener durationChangedListener) {
        this.durationChangedListener = durationChangedListener;
    }


    /**
     * The callback interface used to indicate the duration has been adjusted.
     */
    public interface OnDurationChangedListener {

        /**
         * @param view The view associated with this listener.
         */
        void onDurationChanged(DurationPicker view, int hourOfDay, int minute);
    }

    public DurationPicker(Context context) {
        this(context, null);
    }

    public DurationPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DurationPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.DurationPicker, defStyleAttr, 0);

        final int layoutResourceId = a.getResourceId(R.styleable.DurationPicker_layout, R.layout.duration_picker);

        a.recycle();


        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(layoutResourceId, this, true);
        view.setSaveFromParentEnabled(false);


        //hour
        mHourSpinner = findViewById(R.id.hour);
//        mHourSpinner.setMinValue(0);
//        mHourSpinner.setMaxValue(23);
        mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                onDurationChanged();
            }
        });
        mHourSpinnerInput = mHourSpinner.findViewById(Resources.getSystem().getIdentifier("numberpicker_input", "id", "android"));
        mHourSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);


        // divider (only for the new widget style)
        mDivider = findViewById(R.id.divider);
        if (mDivider != null) {
//            setDividerText();
        }


        // minute
        mMinuteSpinner = findViewById(R.id.minute);
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
//        mMinuteSpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
//                updateInputState();
                int minValue = mMinuteSpinner.getMinValue();
                int maxValue = mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = mHourSpinner.getValue() + 1;
//                    if (!is24Hour() && newHour == HOURS_IN_HALF_DAY) {
//                        mIsAm = !mIsAm;
//                        updateAmPmControl();
//                    }
                    mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour = mHourSpinner.getValue() - 1;
//                    if (!is24Hour() && newHour == HOURS_IN_HALF_DAY - 1) {
//                        mIsAm = !mIsAm;
//                        updateAmPmControl();
//                    }
                    mHourSpinner.setValue(newHour);
                }
                onDurationChanged();
            }
        });
        mMinuteSpinnerInput = mMinuteSpinner.findViewById(Resources.getSystem().getIdentifier("numberpicker_input", "id", "android"));
        mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // update controls to initial state
        updateHourControl();
        updateMinuteControl();

        // set to current time
        mTempCalendar = Calendar.getInstance(new Locale("en", "IN"));
        setHour(mTempCalendar.get(Calendar.HOUR_OF_DAY));
        setMinute(mTempCalendar.get(Calendar.MINUTE));

    }

//    public DurationPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//
//
////        // DatePicker is important by default, unless app developer overrode attribute.
////        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
////            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
////        }
//
//
//    }


    private void onDurationChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (durationChangedListener != null) {
            durationChangedListener.onDurationChanged(this, getHour(), getMinute());
        }
    }

    private void updateHourControl() {
        mHourSpinner.setMinValue(0);
        mHourSpinner.setMaxValue(23);
//        mHourSpinner.setFormatter(mHourWithTwoDigit ? NumberPicker.getTwoDigitFormatter() : null);
    }

    private void updateMinuteControl() {
        mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @InspectableProperty
    public int getHour() {
        return mHourSpinner.getValue();
    }

    @InspectableProperty
    public int getMinute() {
        return mMinuteSpinner.getValue();
    }

    public void setHour(int hour) {
        setCurrentHour(hour, true);
    }

    public void setCurrentHour(int currentHour, boolean notifyTimeChanged) {
        // why was Integer used in the first place?
        if (currentHour == getHour()) {
            return;
        }
//        resetAutofilledValue();
//        if (!is24Hour()) {
//            // convert [0,23] ordinal to wall clock display
//            if (currentHour >= HOURS_IN_HALF_DAY) {
//                mIsAm = false;
//                if (currentHour > HOURS_IN_HALF_DAY) {
//                    currentHour = currentHour - HOURS_IN_HALF_DAY;
//                }
//            } else {
//                mIsAm = true;
//                if (currentHour == 0) {
//                    currentHour = HOURS_IN_HALF_DAY;
//                }
//            }
//            updateAmPmControl();
//        }
        mHourSpinner.setValue(currentHour);
        if (notifyTimeChanged) {
            onDurationChanged();
        }
    }


    public void setMinute(int minute) {
        setCurrentMinute(minute, true);
    }

    public void setCurrentMinute(int minute, boolean notifyTimeChanged) {
        if (minute == getMinute()) {
            return;
        }
//        resetAutofilledValue();
        mMinuteSpinner.setValue(minute);
        if (notifyTimeChanged) {
            onDurationChanged();
        }
    }

}
