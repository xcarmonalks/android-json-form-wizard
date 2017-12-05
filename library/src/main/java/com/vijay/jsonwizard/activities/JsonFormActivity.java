package com.vijay.jsonwizard.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFormActivity extends AppCompatActivity implements JsonApi {

    private static final String TAG = "JsonFormActivity";

    private Toolbar             mToolbar;

    private JSONObject          mJSONObject;

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.d(TAG, "Initialization error. JSON form definition is invalid : " + e.getMessage());
            Intent data = new Intent();
            data.setData(Uri.parse("Initialization error. JSON form definition is invalid"));
            setResult(JsonFormConstants.RESULT_JSON_PARSE_ERROR, data);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureInputMethod();
        boolean launchInit = configureOrientation();
        if (launchInit) {
            initialize();
            createFragments(null);
        }
    }

    protected void initialize() {
        setContentView(R.layout.activity_json_form);
        mToolbar = (Toolbar) findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
    }

    protected void createFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            init(getIntent().getStringExtra("json"));
            if(mJSONObject != null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, JsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
            }
        } else {
            init(savedInstanceState.getString("jsonState"));
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public synchronized JSONObject getStep(String name) {
        synchronized (mJSONObject) {
            try {
                return mJSONObject.getJSONObject(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void writeValue(String stepName, String key, String value) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = jsonObject.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString("key");
                if (key.equals(keyAtIndex)) {
                    item.put("value", value);
                    return;
                }
            }
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value)
            throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = jsonObject.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString("key");
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString("key");
                        if (childKey.equals(anotherKeyAtIndex)) {
                            if(value != null && !"".equals(value)){
                                innerItem.put("value", value);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String currentJsonState() {
        synchronized (mJSONObject) {
            return mJSONObject.toString();
        }
    }

    @Override
    public String getCount() {
        synchronized (mJSONObject) {
            return mJSONObject.optString("count");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mJSONObject != null){
            outState.putString("jsonState", mJSONObject.toString());
        }
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    private void configureInputMethod() {
        if(getIntent().hasExtra(JsonFormConstants.INPUT_METHOD_EXTRA)){
            int inputMethod = getIntent().getIntExtra(JsonFormConstants.INPUT_METHOD_EXTRA, -1);
            if(inputMethod == JsonFormConstants.INPUT_METHOD_VISIBLE){
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }else if(inputMethod == JsonFormConstants.INPUT_METHOD_HIDDEN){
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }
    }

    private boolean configureOrientation() {
        boolean launchInit = true;
        boolean hasOrientationExtra = getIntent().hasExtra(JsonFormConstants.ORIENTATION_EXTRA);
        int currentOrientation = getResources().getConfiguration().orientation;
        int orientation = getIntent().getIntExtra(JsonFormConstants.ORIENTATION_EXTRA, currentOrientation);
        if(hasOrientationExtra){
            launchInit = currentOrientation == orientation;
            if(JsonFormConstants.ORIENTATION_LANDSCAPE == orientation){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if(JsonFormConstants.ORIENTATION_PORTRAIT == orientation){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            getIntent().removeExtra(JsonFormConstants.ORIENTATION_EXTRA);
        }
        return launchInit;
    }
}
