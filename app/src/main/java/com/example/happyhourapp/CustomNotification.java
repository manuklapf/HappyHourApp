package com.example.happyhourapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

public class CustomNotification {
    private static final String CHANNEL_ID = "com.example.happyhourapp";

    private final Context context;
    private final NotificationManager notificationManager;

    public CustomNotification(Context appContext) {
        this.context = appContext;
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(CHANNEL_ID, "Geofence", "Geofencing notifications.");
    }


    private void createChannel(String id, String name, String description) {
        //creates a channel for notifications to be sent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name,
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 100});
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    public void show(int id, String content) {
        Notification.Builder nb;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nb  = new Notification.Builder(context, CHANNEL_ID);
        } else {
            nb  = new Notification.Builder(context);
        }

        // Create an explicit content Intent that starts MainActivity.
        Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivityMap.class);

        // Get a PendingIntent containing the entire back stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
        stackBuilder.addParentStack(MainActivityMap.class).addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = nb.setContentTitle("Happy Hour Bar in der Nähe")
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build();


        notificationManager.notify(id, notification);
    }
}

