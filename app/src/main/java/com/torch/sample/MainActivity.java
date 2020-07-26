package com.torch.sample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.torch.duration_picker.DurationPicker;
import com.torch.duration_picker.DurationPickerDialog;
import com.torch.sample.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DurationPicker durationPicker = findViewById(R.id.durationPicker);
        final TextView textView = findViewById(R.id.text);
        Button showDialog = findViewById(R.id.show_in_dialog_button);

        durationPicker.setDurationChangedListener((view, hourOfDay, minute) ->
                textView.setText(TimeUtils.formatDuration(hourOfDay * 60 + minute)));

        DurationPickerDialog dialog = new DurationPickerDialog(this, (view, hour, minute) -> {
            textView.setText(TimeUtils.formatDuration(hour * 60 + minute));
            durationPicker.setHour(hour);
            durationPicker.setMinute(minute);
        }, durationPicker.getHour(), durationPicker.getMinute());
        showDialog.setOnClickListener(v -> {
            dialog.updateDuration(durationPicker.getHour(), durationPicker.getMinute());
            dialog.show();
        });

        durationPicker.setHour(12);
        durationPicker.setMinute(4);
    }
}