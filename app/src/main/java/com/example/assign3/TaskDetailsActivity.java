package com.example.assign3;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class TaskDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String API_KEY = "YOUR_YOUTUBE_API_KEY";
    private String youtubeUrl;
    private YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        TextView taskTextView = findViewById(R.id.taskTextView);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);

        Intent intent = getIntent();
        String taskName = intent.getStringExtra("taskName");
        youtubeUrl = intent.getStringExtra("youtubeUrl");

        taskTextView.setText(taskName);
        youtubePlayerView.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(extractVideoId(youtubeUrl));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }

    private String extractVideoId(String youtubeUrl) {
        return youtubeUrl.substring(youtubeUrl.lastIndexOf("=") + 1);
    }
}