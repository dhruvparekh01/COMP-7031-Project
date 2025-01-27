package com.example.assign3;

import android.app.Application;

public class TaskReminderManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ReminderScheduler.createNotificationChannel(this);
    }
}