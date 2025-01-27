package com.example.assign3;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class TestReminderActivity extends AppCompatActivity {
    private EditText taskNameInput;
    private EditText youtubeUrlInput;
    private Button setReminderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reminder);

        taskNameInput = findViewById(R.id.taskNameInput);
        youtubeUrlInput = findViewById(R.id.youtubeUrlInput);
        setReminderButton = findViewById(R.id.setReminderButton);

        setReminderButton.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker view, int selectedHour, int selectedMinute) -> {
            scheduleReminder(selectedHour, selectedMinute);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void scheduleReminder(int hour, int minute) {
        String taskName = taskNameInput.getText().toString();
        String youtubeUrl = youtubeUrlInput.getText().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        ReminderScheduler.scheduleReminder(this, taskName, youtubeUrl, calendar);
    }
}