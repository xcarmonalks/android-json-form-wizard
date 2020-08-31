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
    public static final String EXTRA_USE_ACCURACY = "USE_ACCURACY";
    public static final String EXTRA_RESULT_LOCATION = "RESULT_LOCATION";

    private static final String TAG = "JsonFormActivity";
    private static final int REQUEST_CODE_LOCATION = 80;

    private GoogleMap mMap;
    private String mInitialPos;
    private String mMarkerPosition;
    private Marker mMarker;

    private boolean mIncludeAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.tb_top);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.choose_a_location);

        // Load extra datas
        Intent intent = getIntent();
        mIncludeAccuracy = intent.getBooleanExtra(EXTRA_USE_ACCURACY, false);

        if (intent.hasExtra(EXTRA_INITIAL_LOCATION)) {
            try {
                mInitialPos = intent.getStringExtra(EXTRA_INITIAL_LOCATION);
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
            data.putExtra(EXTRA_RESULT_LOCATION, mMarkerPosition);
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
                String position = getPositionWithOptionalAccuracy(new LatLng(0, 0), 0);
                updateMapMarker(position, true);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        mMap.setMaxZoomPreference(MAX_ZOOM_LEVEL);

        if (mInitialPos != null) {
            updateMapMarker(mInitialPos, true);
        }
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMarkerPosition = getPositionWithOptionalAccuracy(mMap.getCameraPosition().target,
                    -1);
                updateMapMarker(mMarkerPosition, false);
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
                String position = getPositionWithOptionalAccuracy(new LatLng(0, 0), 0);
                updateMapMarker(position, true);
            }
        } else {
            FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.getResult() != null) {
                            double lat = task.getResult().getLatitude();
                            double lng = task.getResult().getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            float accuracy = task.getResult().getAccuracy();

                            mInitialPos = getPositionWithOptionalAccuracy(latLng, accuracy);
                            updateMapMarker(mInitialPos, true);
                        } else {
                            Log.w(TAG, "Could not obtain last location", task.getException());
                            String position = getPositionWithOptionalAccuracy(new LatLng(0, 0), 0);
                            updateMapMarker(position, true);
                        }
                    }
                });
        }
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED;
    }

    private void updateMapMarker(String markerPosition, boolean repositionCamera) {
        if (mMap != null) {
            LatLng latLng = MapsUtils.parse(markerPosition);
            if (mMarker == null) {
                mMap.clear();
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            } else {
                mMarker.setPosition(latLng);
            }
            if (repositionCamera) {
                CameraPosition pos = CameraPosition.builder().target(latLng).zoom(MAX_ZOOM_LEVEL)
                                                   .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
            }

            mMarkerPosition = markerPosition;
        }
    }

    private String getPositionWithOptionalAccuracy(LatLng latLng, float accuracy) {
        if (mIncludeAccuracy) {
            return MapsUtils.toString(latLng, accuracy);
        } else {
            return MapsUtils.toString(latLng);
        }
    }

}
