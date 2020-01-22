package com.vijay.jsonwizard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PropertiesUtils {

    private static final String TAG = "PropertiesUtils";
    private static final String PREFERENCES_NAME = "com.vijay.jsonwizard.configuration";

    public static final String PREFERENCE_FORM_ID = "formId";
    public static final String PREFERENCE_FORM_JSON = "formJson";
    public static final String PREFERENCE_FORM_PAUSED_STEP = "pausedStep";

    private static SharedPreferences sharedPreferences;
    private static PropertiesUtils instance;

    private PropertiesUtils(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
    }

    public static synchronized PropertiesUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new PropertiesUtils(ctx);
        }
        return instance;
    }

    public String getFormId() {
        return sharedPreferences.getString(PREFERENCE_FORM_ID, null);
    }

    public void setFormId(String formId) {
        sharedPreferences.edit().putString(PREFERENCE_FORM_ID, formId).commit();
    }

    public String getFormJson() {
        return sharedPreferences.getString(PREFERENCE_FORM_JSON, null);
    }

    public void setFormJson(String formJson) {
        sharedPreferences.edit().putString(PREFERENCE_FORM_JSON, formJson).commit();
    }

    public String getPausedStep() {
        return sharedPreferences.getString(PREFERENCE_FORM_PAUSED_STEP, null);
    }

    public void setPausedStep(String step) {
        sharedPreferences.edit().putString(PREFERENCE_FORM_PAUSED_STEP, step).commit();
    }
}
