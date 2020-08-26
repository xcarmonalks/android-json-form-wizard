package com.vijay.jsonwizard.maps;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import static com.vijay.jsonwizard.maps.MapsUtils.MAX_ZOOM_LEVEL;
import static com.vijay.jsonwizard.maps.MapsUtils.MIN_ZOOM_LEVEL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vijay.jsonwizard.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_INITIAL_LOCATION = "INITIAL_LATITUDE";
    public static final String EXTRA_RESULT_LOCATION = "RESULT_LOCATION";

    private static final String TAG = "JsonFormActivity";
    private static final int REQUEST_CODE_LOCATION = 80;

    private GoogleMap mMap;
    private LatLng mInitialPos;
    private LatLng mMarkerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.tb_top);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.choose_a_location);

        // Load extra datas
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_INITIAL_LOCATION)) {
            try {
                mInitialPos = MapsUtils.parse(intent.getStringExtra(EXTRA_INITIAL_LOCATION));
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Invalid initial position", e);
                attemptMarkCurrentLocation();
            }
        } else {
            // Get current location
            attemptMarkCurrentLocation();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_location_picker, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_choose) {
            Intent data = new Intent();
            data.putExtra(EXTRA_RESULT_LOCATION, MapsUtils.toString(mMarkerPosition));
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            boolean grantedAny = false;
            for (int grantResult: grantResults) {
                if (grantResult == PERMISSION_GRANTED) {
                    grantedAny = true;
                    break;
                }
            }
            if (grantedAny) {
                attemptMarkCurrentLocation();
            } else {
                Log.w(TAG, "Current location permission not granted");
                updateMapMarker(new LatLng(0, 0));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        mMap.setMaxZoomPreference(MAX_ZOOM_LEVEL);

        // Add a marker in Sydney and move the camera
        if (mInitialPos != null) {
            updateMapMarker(mInitialPos);
        }
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMarkerPosition = marker.getPosition();
            }
        });
    }

    private void attemptMarkCurrentLocation() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && !checkPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Request Location permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
            } else {
                // Put marker in default position
                updateMapMarker(new LatLng(0, 0));
            }
        } else {
            FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        double lat = task.getResult().getLatitude();
                        double lng = task.getResult().getLongitude();
                        mInitialPos = new LatLng(lat, lng);

                        updateMapMarker(mInitialPos);
                    }
                });
        }
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED;
    }

    private void updateMapMarker(LatLng markerPosition) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(markerPosition).draggable(true));
            CameraPosition pos = CameraPosition.builder().target(markerPosition).zoom(
                MAX_ZOOM_LEVEL).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

}
