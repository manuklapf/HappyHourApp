package com.example.happyhourapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    protected static final String TAG = "GeofenceReceiver";
    CustomNotification notification;
    @Override
    public void onReceive(Context context, Intent intent) {
        //getting Geofence data
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "error");
            return;
        }
        //checks if user entered a Geofenced zone and throws a notification
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            notification = new CustomNotification(context);
            notification.show(1, "happy hour bar in your area!");

        }

    }
}
