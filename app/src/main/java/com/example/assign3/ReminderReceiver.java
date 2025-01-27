package com.example.assign3;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver{
    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("taskName");
        String youtubeUrl = intent.getStringExtra("youtubeUrl");

        Intent activityIntent = new Intent(context, TaskDetailsActivity.class);
        activityIntent.putExtra("taskName", taskName);
        activityIntent.putExtra("youtubeUrl", youtubeUrl);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Task Reminder")
                .setContentText(taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}