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
 * The type My receiver - BroadcastReceiver for handling and displaying notifications.
 */
public class MyReceiver extends BroadcastReceiver {

    //==========================================================================================
    // DISPLAY LAYER - UI related functionality
    //==========================================================================================

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel (UI component)
        createNotificationChannel(context);

        // Get notification content and PendingIntent from logic layer
        PendingIntent pendingIntent = createPendingIntent(context);

        // Build and display the notification (UI component)
        displayNotification(context, pendingIntent);
    }

    /**
     * Creates a notification channel for Android 8.0 (Oreo) and above
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel",
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel 1...");

            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Builds and displays the notification to the user
     */
    private void displayNotification(Context context, PendingIntent pendingIntent) {
        // Build the notification with UI components
        Notification notification = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Title")
                .setContentText("Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Get the NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Only display if we have permission
        if (hasNotificationPermission(context)) {
            // Show notification with ID 1
            notificationManager.notify(1, notification);
        }
    }

    //==========================================================================================
    // LOGIC LAYER - Business logic and data processing
    //==========================================================================================

    /**
     * Creates a PendingIntent for the notification
     */
    private PendingIntent createPendingIntent(Context context) {
        // Create intent for HomeFragment
        Intent notificationIntent = new Intent(context, HomeFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create and return PendingIntent with proper flags for security
        return PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /**
     * Checks if the app has notification permission
     */
    private boolean hasNotificationPermission(Context context) {
        // For Android 13+ (API 33+), check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // For older versions, permission is granted at install time
        return true;
    }
}