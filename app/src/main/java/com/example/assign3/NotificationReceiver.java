package com.example.assign3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class    NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskNotes = intent.getStringExtra("taskNotes");
        int taskId = intent.getIntExtra("taskId", -1);

        Intent notificationIntent = new Intent(context, TaskDetailActivity.class);
        notificationIntent.putExtra("taskId", taskId);
        notificationIntent.putExtra("taskTitle", taskTitle);
        notificationIntent.putExtra("taskNotes", taskNotes);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_REMINDER")
                .setSmallIcon(R.drawable.ic_notification) // Ensure you have an icon in res/drawable
                .setContentTitle(taskTitle)
                .setContentText(taskNotes)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(taskId, builder.build());
        }
    }
}
