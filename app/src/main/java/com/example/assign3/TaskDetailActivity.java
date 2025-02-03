package com.example.assign3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.ClientDetails;
import com.example.assign3.apiClient.model.TaskDetails;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TaskDetailActivity extends AppCompatActivity {
    TextView clientName, reminderName, taskType, dateTime, repeatDays, notes, filePath;
    Spinner dropdown;
    ApiClient apiClient;
    String authTok;
    int taskId;
    TaskDetails taskDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);
        apiClient = ApiClient.getInstance();
        taskId = getIntent().getIntExtra("taskId", 2);
        authTok = getIntent().getStringExtra("authTok");


        initializeViews();
        getTaskDetails();
        setClientDetails();
    }

    private void initializeViews() {
        clientName = findViewById(R.id.tvClientName);
        reminderName = findViewById(R.id.tvReminderName);
        taskType = findViewById(R.id.tvTaskType);
        dateTime = findViewById(R.id.tvDateTime);
        repeatDays = findViewById(R.id.tvRepeatDays);
        notes = findViewById(R.id.tvNotes);
        filePath = findViewById(R.id.tvFilePath);


        // Back button click listener
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            // Go back to the previous activity
            finish();
        });
    }

    private void getTaskDetails() {
        CompletableFuture<TaskDetails> futureData = apiClient.getTaskDetails(taskId, authTok);
        futureData.thenAccept(taskDetails -> {
            runOnUiThread(() -> {
                this.taskDetails = taskDetails;
                setClientDetails();
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(TaskDetailActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void setClientDetails() {
        if (taskDetails != null) {
            clientName.setText(taskDetails.getReminderName());
            reminderName.setText(taskDetails.getReminderName());
            taskType.setText(taskDetails.getTaskType());
            dateTime.setText(taskDetails.getDateTime());
            repeatDays.setText(taskDetails.getRepeatDays());
            notes.setText(taskDetails.getNotes());
            filePath.setText(taskDetails.getFilePath());
        } else {
            Toast.makeText(TaskDetailActivity.this, "No data available", Toast.LENGTH_SHORT).show();
        }
    }
}