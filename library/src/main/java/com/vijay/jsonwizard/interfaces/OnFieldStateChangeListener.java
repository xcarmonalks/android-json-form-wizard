package com.vijay.jsonwizard.interfaces;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 20/10/17.
 */

public interface OnFieldStateChangeListener {

    void onInitialValueSet(String parentKey, String childKey, String value);

    void onValueChange(String parentKey, String childKey, String value);

    void onVisibilityChange(String key, String o, boolean b);

}
