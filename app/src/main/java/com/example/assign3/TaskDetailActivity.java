package com.example.assign3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assign3.apiClient.ApiClient;
import com.example.assign3.apiClient.model.TaskDetails;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TaskDetailActivity extends AppCompatActivity {
    TextView clientName, reminderName, taskType, dateTime, repeatDays, notes, filePath;
    ApiClient apiClient;
    String authTok;
    int taskId;
    TaskDetails taskDetails;

    private ExoPlayer player;
    private PlayerView playerView;
    private String videoUrl; // Video file path or URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        apiClient = ApiClient.getInstance();
        playerView = findViewById(R.id.exoPlayerView); // Ensure your XML has this ID

        taskId = getIntent().hasExtra("taskId") ? getIntent().getIntExtra("taskId", -1) : -1;
        if (taskId == -1) {
            Toast.makeText(this, "Error: Task ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        authTok = getIntent().getStringExtra("authTok");

        initializeViews();
        getTaskDetails();
    }

    private void initializeViews() {
        clientName = findViewById(R.id.tvClientName);
        reminderName = findViewById(R.id.tvReminderName);
        taskType = findViewById(R.id.tvTaskType);
        dateTime = findViewById(R.id.tvDateTime);
        repeatDays = findViewById(R.id.tvRepeatDays);
        notes = findViewById(R.id.tvNotes);
        filePath = findViewById(R.id.tvFilePath);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish()); // Back button logic
    }

    private void getTaskDetails() {
        CompletableFuture<TaskDetails> futureData = apiClient.getTaskDetails(taskId, authTok);
        futureData.thenAccept(taskDetails -> runOnUiThread(() -> {
            this.taskDetails = taskDetails;
            setClientDetails();
        })).exceptionally(throwable -> {
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
            notes.setText(taskDetails.getNotes());
            filePath.setText(taskDetails.getFilePath());

            // Debugging: Print the raw repeat days value
            String rawRepeatDays = taskDetails.getRepeatDays();
            System.out.println("Raw Repeat Days: " + rawRepeatDays);

            // Fix: Display repeat days properly
            if (!taskDetails.getRepeatDays().isEmpty()) {
                String repeatDaysFormatted = formatRepeatDays(taskDetails.getRepeatDays());
                repeatDays.setText(repeatDaysFormatted);
            } else {
                repeatDays.setText("No Repeat Days");
            }

            // Fix: Initialize video player if video URL is available
            videoUrl = taskDetails.getFilePath();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                initializePlayer(videoUrl);
            } else {
                Toast.makeText(this, "No video available for this task", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(TaskDetailActivity.this, "No data available", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializePlayer(String videoPath) {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoPath);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
    private String formatRepeatDays(String repeatDaysRaw) {
        // Example input: "1,3,5"  (Days represented as numbers)
        String[] daysArray = repeatDaysRaw.split(",");

        // Mapping numbers to actual day names
        HashMap<String, String> dayMapping = new HashMap<>();
        dayMapping.put("1", "Monday");
        dayMapping.put("2", "Tuesday");
        dayMapping.put("3", "Wednesday");
        dayMapping.put("4", "Thursday");
        dayMapping.put("5", "Friday");
        dayMapping.put("6", "Saturday");
        dayMapping.put("7", "Sunday");

        List<String> dayNames = new ArrayList<>();
        for (String day : daysArray) {
            if (dayMapping.containsKey(day.trim())) {
                dayNames.add(dayMapping.get(day.trim()));
            }
        }

        return dayNames.isEmpty() ? "No Repeat Days" : TextUtils.join(", \n", dayNames);
    }

}
