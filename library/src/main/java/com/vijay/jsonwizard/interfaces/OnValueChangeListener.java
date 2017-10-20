package com.vijay.jsonwizard.interfaces;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 20/10/17.
 */

public interface OnValueChangeListener {
    void onInitialValueSet(String parentKey, String childKey, String value);
    void onValueChange(String parentKey, String childKey, String value);
}
