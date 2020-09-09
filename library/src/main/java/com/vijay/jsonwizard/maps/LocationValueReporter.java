package com.vijay.jsonwizard.maps;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONException;

public class LocationValueReporter {

    private static final String TAG = "JsonFormActivity";

    private final String mStepName;
    private final View mView;
    private final boolean mAccuracyEnabled;

    private String mLatitude = "";
    private String mLongitude = "";
    private String mAccuracy = "";

    public LocationValueReporter(String stepName, View view, boolean accuracyEnabled) {
        mStepName = stepName;
        mView = view;
        mAccuracyEnabled = accuracyEnabled;
    }

    public LocationValueReporter(String stepName, View view, String initialValue, boolean accuracyEnabled) {
        mStepName = stepName;
        mView = view;
        mAccuracyEnabled = accuracyEnabled;

        String[] parts = initialValue.split(MapsUtils.COORD_SEPARATOR);
        if (parts.length > 0) {
            mLatitude = parts[0].trim();
        }
        if (parts.length > 1) {
            mLongitude = parts[1].trim();
        }
        if (parts.length > 2) {
            mAccuracy = parts[2].trim();
        }
    }

    public void reportValue(String partialText, LocationPart locationComponent) {
        switch (locationComponent) {
            case LATITUDE:
                mLatitude = partialText;
                break;
            case LONGITUDE:
                mLongitude = partialText;
                break;
            case ACCURACY:
                mAccuracy = partialText;
                break;
        }
        String text;
        if (mAccuracyEnabled) {
            text = MapsUtils.toString(mLatitude, mLongitude, mAccuracy);
        } else {
            text = MapsUtils.toString(mLatitude, mLongitude);
        }

        JsonApi api;
        Context ctx = mView.getContext();
        if (ctx instanceof JsonApi) {
            api = (JsonApi) ctx;
        } else if (ctx instanceof ContextWrapper) {
            ContextWrapper contextWrapper = (ContextWrapper) ctx;
            api = (JsonApi) contextWrapper.getBaseContext();
        } else {
            throw new RuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        try {
            api.writeValue(mStepName, key, text);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

}
