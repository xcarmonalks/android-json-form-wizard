package com.vijay.jsonwizard.maps;

import android.text.Editable;
import android.text.TextWatcher;

public class LocationTextWatcher implements TextWatcher {

    private final LocationValueReporter mValueReporter;
    private final LocationPart mLocationComponent;

    public LocationTextWatcher(LocationPart locationComponent, LocationValueReporter valueReporter) {
        mLocationComponent = locationComponent;
        mValueReporter = valueReporter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        mValueReporter.reportValue(text, mLocationComponent);
    }
}
