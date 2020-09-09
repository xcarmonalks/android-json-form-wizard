package com.vijay.jsonwizard.maps;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vijay.jsonwizard.R;

public class MapsUtils {

    public static final float MAX_ZOOM_LEVEL = 18f;
    public static final float MIN_ZOOM_LEVEL = 8f;

    public static final String COORD_SEPARATOR = ",";

    private static final String TAG = "JsonFormsActivity";

    public static LatLng parse(String latLng) {
        String[] parts = latLng.split(COORD_SEPARATOR);
        if (parts.length != 2 && parts.length != 3) {
            throw new IllegalArgumentException("Invalid coordinate string: " + latLng);
        }
        return new LatLng(Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()));
    }

    public static String toString(LatLng latLng) {
        return String.format("%s, %s", latLng.latitude, latLng.longitude);
    }

    public static String toString(LatLng latLng, float accuracy) {
        return String.format("%s, %s, %s", latLng.latitude, latLng.longitude, accuracy);
    }

    public static String toString(String latitude, String longitude) {
        return String.format("%s, %s", latitude, longitude);
    }

    public static String toString(String latitude, String longitude, String accuracy) {
        return String.format("%s, %s, %s", latitude, longitude, accuracy);
    }

    public static void loadStaticMap(FragmentActivity activity, String key, String value) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        loadStaticMap(fragmentManager, key, value);
    }

    public static void loadStaticMap(Fragment fragment, String key, String value) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        loadStaticMap(fragmentManager, key, value);
    }

    private static void loadStaticMap(FragmentManager fragmentManager, String key, String value) {
        try {
            Log.d(TAG, "Updating map");
            final LatLng position = MapsUtils.parse(value);


            Log.d(TAG, "Creating new map");
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            GoogleMapOptions options = new GoogleMapOptions();
            options.scrollGesturesEnabled(false);
            options.zoomGesturesEnabled(false);
            options.rotateGesturesEnabled(false);

            SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

            transaction.replace(R.id.map_container, mapFragment, key);
            transaction.commit();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d(TAG, "Map ready, adding marker in : " + position.toString());
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(position));
                    CameraPosition pos = CameraPosition.builder().target(position).zoom(
                        MapsUtils.MAX_ZOOM_LEVEL).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "Invalid GPS value: " + value, e);
        }
    }


    public static boolean isValidPositionString(String value) {
        if (value == null) {
            return false;
        }

        String[] parts = value.split(COORD_SEPARATOR);
        if (parts.length != 2 && parts.length != 3) {
            return false;
        }

        try {
            Double.parseDouble(parts[0]);
            Double.parseDouble(parts[1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
