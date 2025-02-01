package com.example.assign3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        int clientId = intent.getIntExtra("clientId", 0);
        String clientName = intent.getStringExtra("clientName");

        TextView taskInfoTextView = findViewById(R.id.clientNameTask); // Assuming you have a TextView with this ID
        taskInfoTextView.setText(clientName);

        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> {
            // Go back to the previous activity
            finish();
        });

        // Add Task button logic
        findViewById(R.id.addTask).setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissal by clicking outside
                .create();

        // Find views in the dialog
        EditText reminderName = dialogView.findViewById(R.id.reminderName);
        Button selectDateTimeButton = dialogView.findViewById(R.id.selectDateTimeButton);
        TextView selectedDateTimeText = dialogView.findViewById(R.id.selectedDateTimeText);
        CheckBox repeatSunday = dialogView.findViewById(R.id.repeatSunday);
        CheckBox repeatMonday = dialogView.findViewById(R.id.repeatMonday);
        CheckBox repeatTuesday = dialogView.findViewById(R.id.repeatTuesday);
        CheckBox repeatWednesday = dialogView.findViewById(R.id.repeatWednesday);
        CheckBox repeatThursday = dialogView.findViewById(R.id.repeatThursday);
        CheckBox repeatFriday = dialogView.findViewById(R.id.repeatFriday);
        CheckBox repeatSaturday = dialogView.findViewById(R.id.repeatSaturday);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button addTaskButton = dialogView.findViewById(R.id.addTaskButton);

        // Calendar instance for Date-Time Picker
        Calendar calendar = Calendar.getInstance();

        // Date-Time Picker logic
        selectDateTimeButton.setOnClickListener(v -> {
            // Open DatePickerDialog
            new DatePickerDialog(TaskActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);

                // Open TimePickerDialog after selecting the date
                new TimePickerDialog(TaskActivity.this, (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Display the selected Date and Time
                    selectedDateTimeText.setText("Selected: " + calendar.getTime());
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Cancel button logic
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Add Task button logic
        addTaskButton.setOnClickListener(v -> {
            String name = reminderName.getText().toString();
            String dateTime = selectedDateTimeText.getText().toString();

            if (name.isEmpty() || dateTime.equals("No date and time selected")) {
                Toast.makeText(TaskActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String repeatDays = (repeatSunday.isChecked() ? "Sun " : "") +
                    (repeatMonday.isChecked() ? "Mon " : "") +
                    (repeatTuesday.isChecked() ? "Tue " : "") +
                    (repeatWednesday.isChecked() ? "Wed " : "") +
                    (repeatThursday.isChecked() ? "Thu " : "") +
                    (repeatFriday.isChecked() ? "Fri " : "") +
                    (repeatSaturday.isChecked() ? "Sat " : "");

            // Process the task details (e.g., save to a database)
            Toast.makeText(TaskActivity.this, "Task added: " + name, Toast.LENGTH_SHORT).show();

            dialog.dismiss();

        });

        dialog.show();
    }
}
