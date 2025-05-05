package com.example.asaf_avisar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * The type My receiver.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel for API 26+ (Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("channel", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1...");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel1);
            }
        }

        // Create an intent for opening the HomeFragment activity
        Intent notificationIntent = new Intent(context, HomeFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Use FLAG_IMMUTABLE for PendingIntent security
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create notification
        Notification notification = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a default icon or your own
                .setContentTitle("Title")
                .setContentText("Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Get the NotificationManager and notify the user
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Request permission if necessary (for Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return; // You can also request permission here if needed
            }
        }

        // Notify with a unique ID (1 here)
        notificationManager.notify(1, notification);
    }
}
