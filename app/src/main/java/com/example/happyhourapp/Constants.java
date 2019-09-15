package com.example.happyhourapp;
        import com.google.android.gms.maps.model.LatLng;
        import java.util.HashMap;

public class Constants {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 20;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {
        //Bars get added here
        LANDMARKS.put("Bar1", new LatLng(49.018178,12.096371));
        LANDMARKS.put("Bar2", new LatLng(49.018122,12.105508));
    }

}
