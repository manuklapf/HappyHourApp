package com.example.happyhourapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
