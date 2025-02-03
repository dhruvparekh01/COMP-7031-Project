package com.example.assign3;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.PostClientResponse;
import com.example.assign3.apiClient.model.PostTaskResponse;
import com.example.assign3.apiClient.model.Task;
import com.example.assign3.apiClient.model.TaskDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import okhttp3.Request;

public class TaskActivity extends AppCompatActivity {
    private ApiClient apiClient;
    private String authTok;
    private int clientId;
    String date_time;
    TextView selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        clientId = intent.getIntExtra("clientId", 0);
        String clientName = intent.getStringExtra("clientName");
        authTok = getIntent().getStringExtra("authTok");
        apiClient = ApiClient.getInstance();

        TextView taskInfoTextView = findViewById(R.id.clientNameTask); // Assuming you have a TextView with this ID
        taskInfoTextView.setText(clientName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Exact Alarm Permission Required")
                        .setMessage("This app needs permission to schedule exact alarms for reminders. Please enable it in settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent_2 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent_2);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }


        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> {
            // Go back to the previous activity
            finish();
        });

        populateList();

        // Add Task button logic
        findViewById(R.id.addTask).setOnClickListener(v -> showAddTaskDialog());
        createNotificationChannel();
    }

    private void populateSpinner(Spinner taskTypeSpinner) {
        String[] taskTypes = {"Fitness", "Repairing", "Homework"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskTypeSpinner.setAdapter(adapter);
    }


    private void populateList() {

        CompletableFuture<List<Task>> futureData = apiClient.getTasks(clientId, authTok);

        futureData.thenAccept(taskList -> {
            runOnUiThread(() -> {
                // Update the UI with the list of tasks
                // For example, update a RecyclerView with the taskList
                RecyclerView recyclerView = findViewById(R.id.taskList);

                if (recyclerView.getLayoutManager() == null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                }
                // Set the adapter for the RecyclerView
                recyclerView.setAdapter(new TaskAdapter(taskList, this));
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            return null;
        });

    }

    private void uploadTaskToDb(TaskDetails taskDetails) {
        // Upload task details to a database by calling /tasks POST endpoint

        CompletableFuture<PostTaskResponse> futureData = apiClient.postTaskDetails(taskDetails, authTok);

        futureData.thenAccept(response -> {
            if(response.getResponseCode() == 200) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Successully added client", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else if (response.getResponseCode() == 409) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Client already exists", Toast.LENGTH_SHORT).show();
                });
            }
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            return null;
        });

    }

    // Helper method to get the file path from the URI (may vary based on your Android version)
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

    // Handle the result when the user selects a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the URI of the selected video file
            if (data != null) {
                Uri selectedUri = data.getData();
                String filePath = getFilePathFromUri(selectedUri);
                selectedFilePath.setText(filePath); // Display the file path in the TextView
            }
        }
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
        EditText notesField = dialogView.findViewById(R.id.notesField);
        Button selectFileButton = dialogView.findViewById(R.id.selectFileButton);
        selectedFilePath = dialogView.findViewById(R.id.selectedFilePath);

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

        Spinner taskTypeSpinner = dialogView.findViewById(R.id.taskTypeSpinner);
        populateSpinner(taskTypeSpinner);

        selectFileButton.setOnClickListener(v -> {
            // Create an intent to open the file explorer
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*"); // Filter for video files
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Start the activity and wait for the result
            startActivityForResult(Intent.createChooser(intent, "Select a video"), 1);
        });

        // Date-Time Picker logic
        selectDateTimeButton.setOnClickListener(v -> {
            // Open DatePickerDialog
            new DatePickerDialog(TaskActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);

                // Open TimePickerDialog after selecting the date
                new TimePickerDialog(TaskActivity.this, (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    date_time = sdf.format(calendar.getTime()); // Now it's a String

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
            String notes = notesField.getText().toString();
            String taskType = taskTypeSpinner.getSelectedItem().toString();
            String filePath = selectedFilePath.getText().toString();


            if (name.isEmpty() || dateTime.equals("No date and time selected")) {
                Toast.makeText(TaskActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a list to store selected days
            ArrayList<String> selectedDays = new ArrayList<>();

            // Add checked days to the list
            if (repeatSunday.isChecked()) selectedDays.add("Sun");
            if (repeatMonday.isChecked()) selectedDays.add("Mon");
            if (repeatTuesday.isChecked()) selectedDays.add("Tue");
            if (repeatWednesday.isChecked()) selectedDays.add("Wed");
            if (repeatThursday.isChecked()) selectedDays.add("Thu");
            if (repeatFriday.isChecked()) selectedDays.add("Fri");
            if (repeatSaturday.isChecked()) selectedDays.add("Sat");

            // Join the list into a comma-separated string
            String repeatDays = TextUtils.join(",", selectedDays);

            TaskDetails taskDetails = new TaskDetails(clientId, name, taskType, date_time, repeatDays, notes, filePath);

            // Process the task details (e.g., save to a database)
            Toast.makeText(TaskActivity.this, "Task added: " + name, Toast.LENGTH_SHORT).show();

            uploadTaskToDb(taskDetails);

            // Convert selected date-time to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long reminderTime = 0;
            try {
                Date date = sdf.parse(date_time);
                if (date != null) {
                    reminderTime = date.getTime();
                }
            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }
            // Schedule notification at the selected time
            scheduleNotification(name, notes, reminderTime, taskDetails.getId());

            dialog.dismiss();

            populateList();

        });

        dialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskReminderChannel";
            String description = "Channel for task reminder notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TASK_REMINDER", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(String taskTitle, String taskDetails, long triggerTime, int taskId) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskDetails", taskDetails);
        intent.putExtra("taskId", taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                Toast.makeText(this, "Exact alarm permission required. Enable it in settings.", Toast.LENGTH_LONG).show();
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

    }
}
