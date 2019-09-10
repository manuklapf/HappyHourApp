package com.example.happyhourapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivityMap extends FragmentActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


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
    private GoogleMap mMap;
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

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }

        //Initialize Weekday Dropdown
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        return mMarker;
    }

    private String createHappyHourInfoForBar(HappyHour[] happyHours, String selectedDay, Bar bar) {
        StringBuilder stringBuilder = new StringBuilder();

        //create Happy Hours Info for selected day
        if (happyHours != null) {
            for (HappyHour happyHour : happyHours) {
                if (happyHour.getBarId() == bar.getBarId() && happyHour.getHappyHourDay().equals(selectedDay)) {
                    stringBuilder.append("\n" + "Day: " + happyHour.getHappyHourDay() + "\n" +
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
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //Koordinaten werden im Textview gespeichert (wird später geändert)
                    //textView.append("\n" +intent.getExtras().get("Coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //broadcastreceiver wird beendet wenn die App geschlossen wird
        if (broadcastReceiver != null) {
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
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

        Toast.makeText(this, sSelected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}