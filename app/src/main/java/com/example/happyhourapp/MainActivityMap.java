package com.example.happyhourapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyhourapp.models.Bar;
import com.example.happyhourapp.models.BarAndAllHappyHours;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivityMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private LocationService listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 20000; /* 20 sec */
    private static int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private BroadcastReceiver broadcastReceiver;
    private boolean isPermissions;
    private ArrayList<BarAndAllHappyHours> bars;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        if (requestLocationPermissions()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }

        startLocationService();
    }

    private AppDatabase initDatabase() {
            AppDatabase db;
            try {
                db = AppDatabase.getAppDatabase(this);
                return db;
            } catch (Exception e) {
                Log.e(TAG, "database problem!");
                e.printStackTrace();
                return null;
            }

    }

    /**
     * Start location service to fetch own location data.
     */
    private void startLocationService() {
        Intent i = new Intent(getApplicationContext(), com.example.happyhourapp.LocationService.class);
        startService(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        AppDatabase db = initDatabase();

        Bar[] bars = db.barDAO().loadAllBars();

        if (bars != null) {
            for (Bar bar: bars) {
                double barLat = bar.getLocation().getLatitude();
                double barLng = bar.getLocation().getLongitude();
                LatLng latLng = new LatLng(barLat, barLng);
                mMap.addMarker(new MarkerOptions().position(latLng).title(bar.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }


    }


    /**
     * Init Mock Bar Markers.
     */
    private void initBarMarkers() {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (BarAndAllHappyHours bar: this.bars){
            double barLat = bar.getBar().getLocation().getLatitude();
            double barLng = bar.getBar().getLocation().getLongitude();
            if (barLat != 0 && barLng != 0) {
                LatLng latLng = new LatLng(barLat, barLng);
                this.mMap.addMarker(new MarkerOptions().position(latLng));
                this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            else {
                Log.e(TAG, "Error with bar location");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
            }
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    //broadcast receiver is initialized
    @Override
    protected void onResume() {
        super.onResume();
        //falls es keinen Broadcastreceiver gibt wird hier einer erstellt
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //Koordinaten werden im Textview gespeichert (wird später geändert)
                    //textView.append("\n" +intent.getExtras().get("Coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //broadcastreceiver wird beendet wenn die App geschlossen wird
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }



    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION );
            }return;
        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
//                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    /**
     * Check Permissions for Map and GPS usage.
     * @return boolean
     */
    private boolean requestLocationPermissions() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,   Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // permission granted
                            Toast.makeText(MainActivityMap.this, "Location permissions granted!", Toast.LENGTH_SHORT).show();
                            isPermissions = true;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently
                            isPermissions = false;
                            Toast.makeText(MainActivityMap.this, "Location permissions not granted, please grant permissions in app settings!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

        return isPermissions;

    }
}