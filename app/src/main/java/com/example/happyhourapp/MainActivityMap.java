package com.example.happyhourapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import android.location.LocationListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyhourapp.models.Bar;
import com.example.happyhourapp.models.HappyHour;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivityMap extends FragmentActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {


    protected ArrayList<Geofence> mGeofenceList;
    private GoogleMap mMap;
    Marker userMarker;
    private LatLng MyCoordinates;
    private static final String TAG = "MainActivity";
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
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private String selectedDay;
    private HappyHour[] happyHours;
    private Bar[] bars;

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
            mGoogleApiClient.connect();
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            requestLocation();
            geofencingClient = LocationServices.getGeofencingClient(this);
            mGeofenceList = new ArrayList<Geofence>();
            populateGeofenceList();
        }

        //Initialize Weekday Dropdown
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.weekdays_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        selectedDay = getWeekday();

        if (selectedDay.length() != 0) {
            int spinnerPosition = adapter.getPosition(selectedDay);
            spinner.setSelection(spinnerPosition);
        }

        startLocationService();
    }

    /**
     * Get current weekday as String
     *
     * @return String
     */
    private String getWeekday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String currentDay;

        switch (day) {
            case Calendar.MONDAY:
                currentDay = "Monday";
                break;
            case Calendar.TUESDAY:
                currentDay = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                currentDay = "Wednesday";
                break;
            case Calendar.THURSDAY:
                currentDay = "Thursday";
                break;
            case Calendar.FRIDAY:
                currentDay = "Friday";
                break;
            case Calendar.SATURDAY:
                currentDay = "Saturday";
                break;
            case Calendar.SUNDAY:
                currentDay = "Sunday";
                break;
            default:
                return "";
        }
        return currentDay;
    }

    /**
     * Inits Database
     *
     * @return AppDatabase
     */
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

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        AppDatabase db = initDatabase();

        //normally database actions wouldn't be executed on the UI thread, but because it's a small one time start operation, we went with it.
        this.happyHours = db.happyHourDAO().loadAllHappyHours();
        this.bars = db.barDAO().loadAllBars();

        if (bars != null) {
            for (Bar bar : this.bars) {
                String happyHourText = createHappyHourInfoForBar(this.happyHours, this.selectedDay, bar);

                LatLng latLng = getBarLatLng(bar);

                String address = getBarAddress(latLng);

                //set text of custom window view
                String snippet =
                                "Address: " + address + "\n" +
                                "Opening Hours: " + bar.getOpeningHours() + "\n" +
                                "Features: " + bar.getDescription() + "\n" +
                                "Happy Hours: " + happyHourText + "\n";

                Marker mMarker = updateMarker(bar, latLng, snippet);

                if (mMarker != null) {
                    if (happyHourText.length() == 0) {
                        mMarker.setVisible(false);
                    } else {
                        mMarker.setVisible(true);
                    }
                }
            }
        }
    }

    private void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //doesn't trigger notifications if user is in a geofenced zone on startup
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void populateGeofenceList() {
        //adds Geofences from Constants
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private String getBarAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address.length() == 0) {
            address = " - ";
        }

        return address;
    }

    private LatLng getBarLatLng(Bar bar) {
        double barLat = bar.getLocation().getLatitude();
        double barLng = bar.getLocation().getLongitude();
        return new LatLng(barLat, barLng);
    }

    private Marker updateMarker(Bar bar, LatLng latLng, String snippet) {
        Marker mMarker;
        try {

            //set marker options
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(bar.getName())
                    .snippet(snippet);

            mMarker = mMap.addMarker(options);


        } catch (NullPointerException e) {
            Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            return null;
        }
        return mMarker;
    }

    private String createHappyHourInfoForBar(HappyHour[] happyHours, String selectedDay, Bar bar) {
        StringBuilder stringBuilder = new StringBuilder();

        //create Happy Hours Info for selected day
        if (happyHours != null) {
            for (HappyHour happyHour : happyHours) {
                if (happyHour.getBarId() == bar.getBarId() && happyHour.getHappyHourDay().equals(selectedDay)) {
                    stringBuilder.append("\n" +
                            "Day: " + happyHour.getHappyHourDay() + "\n" +
                            "Time: " + happyHour.getHappyHourTime() + "\n" +
                            "Description: " + happyHour.getHappyHourDesc() + "\n"
                    );
                }
            }
            if (stringBuilder.toString().length() != 0) {
                return stringBuilder.toString();
            }
        }
        return "";
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
            //adds Geofences if permissions are requested and if user is connected
            addGeofences();
            return;
        }
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
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

    @Override
    public void onLocationChanged(Location location) {
        if (userMarker != null) {
            userMarker.remove();
        }
        MyCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        userMarker = mMap.addMarker(new MarkerOptions().position(MyCoordinates).title("My Current Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(MyCoordinates));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 10000, 10, this);
        if (MyCoordinates == null) {
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
            MyCoordinates = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }
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
     *
     * @return boolean
     */
    private boolean requestLocationPermissions() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
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

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Geofences added");
        } else {
            Log.e(TAG, "error with Geofences");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sSelected = adapterView.getItemAtPosition(i).toString();
        this.selectedDay = sSelected;

        mMap.clear();
        //refresh every happy hour description and set new markers
        if (this.bars != null) {
            for (Bar bar : this.bars) {
                String happyHourText = createHappyHourInfoForBar(this.happyHours, selectedDay, bar);

                LatLng latLng = getBarLatLng(bar);
                String address = getBarAddress(latLng);

                //set text of custom window view
                String snippet =
                                "Address: " + address + "\n" +
                                "Opening Hours: " + bar.getOpeningHours() + "\n" +
                                "Features: " + bar.getDescription() + "\n" +
                                "Happy Hours: " + happyHourText;

                //update/add marker
                Marker mMarker = updateMarker(bar, latLng, "");

                if (mMarker != null) {
                    mMarker.setSnippet(snippet);

                    if (happyHourText.length() == 0) {
                        mMarker.setVisible(false);
                    } else {
                        mMarker.setVisible(true);
                    }
                }
            }
        }
        if (MyCoordinates != null) {
            //reset usermarker
            userMarker = mMap.addMarker(new MarkerOptions().position(MyCoordinates).title("My Current Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}