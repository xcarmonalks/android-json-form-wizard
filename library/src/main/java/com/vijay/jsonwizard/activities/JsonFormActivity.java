package com.vijay.jsonwizard.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.AssetsResourceResolver;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.demo.resources.ResourceResolverFactory;
import com.vijay.jsonwizard.expressions.ExternalContentResolver;
import com.vijay.jsonwizard.expressions.ExternalContentResolverFactory;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.state.StateProvider;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.PropertiesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.vijay.jsonwizard.constants.JsonFormConstants.MAX_PARCEL_SIZE;
import static com.vijay.jsonwizard.state.StateContract.COL_JSON;

public class JsonFormActivity extends AppCompatActivity implements JsonApi {

    private static final String TAG = "JsonFormActivity";

    private static final String PROP_HISTORY = "_history";

    private Toolbar mToolbar;

    private JSONObject mJSONObject;
    private int mVisualizationMode;
    private JsonFormBundle mJsonBundle;
    private JsonExpressionResolver mResolver;
    private ResourceResolver mResourceResolver;
    private ExternalContentResolver mContentResolver;
    private String externalContentResolverClass;
    private String resourceResolverClass;

    public void init(String json, Integer visualizationMode, String externalContentResolverClass,
        String resourceResolverClass) {
        this.externalContentResolverClass = externalContentResolverClass;
        this.resourceResolverClass = resourceResolverClass;
        if (!TextUtils.isEmpty(externalContentResolverClass)) {
            this.mContentResolver = ExternalContentResolverFactory.getInstance(this, externalContentResolverClass);
        }

        if (TextUtils.isEmpty(resourceResolverClass)) {
            this.resourceResolverClass = AssetsResourceResolver.class.getName();
        }
        this.mResourceResolver = ResourceResolverFactory.getInstance(this, this.resourceResolverClass);
        init(json, visualizationMode);
    }

    public void init(String json, Integer visualizationMode) {
        try {
            mJSONObject = new JSONObject(json);
            mVisualizationMode = visualizationMode;
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
        configureOrientation();
        initialize();
        createFragments(savedInstanceState);
    }

    protected void initialize() {
        setContentView(R.layout.activity_json_form);
        mToolbar = (Toolbar) findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
    }

    protected void createFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            String contentResolver = getIntent().getStringExtra("resolver");
            String resourceResolver = getIntent().getStringExtra("resourceResolver");
            String formJson = getIntent().getStringExtra("json");
            if (formJson == null && getIntent().getParcelableExtra("jsonUri") != null) {
                Uri jsonUri = getIntent().getParcelableExtra("jsonUri");
                try (Cursor c = getContentResolver().query(jsonUri, null, null, null, null)) {
                    if (c != null && c.moveToFirst()) {
                        formJson = c.getString(c.getColumnIndex(COL_JSON));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Could not resolve JsonForm URI: " + jsonUri, e);
                }
            }
            init(formJson, getIntent()
                    .getIntExtra(JsonFormConstants.VISUALIZATION_MODE_EXTRA, JsonFormConstants.VISUALIZATION_MODE_EDIT),
                contentResolver, resourceResolver);
            if (mJSONObject != null) {
                String step = JsonFormConstants.FIRST_STEP_NAME;
                String pausedStep = getIntent().getStringExtra("pausedStep");

                getSupportFragmentManager().beginTransaction().add(R.id.container,
                        JsonFormFragment.getFormFragment(step)).commit();
                try {
                    if(pausedStep != null && !"".equals(pausedStep)){
                        while(!pausedStep.equals(step)) {
                            step = JsonFormUtils.resolveNextStep(mJSONObject.getJSONObject(step), getExpressionResolver(), mJSONObject);
                            JsonFormFragment nStep = JsonFormFragment.getFormFragment(step);
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, nStep)
                                    .addToBackStack(nStep.getClass().getSimpleName()).commit();
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onNextClick: Error evaluating next step", e);
                }
            }
        } else {
            init(savedInstanceState.getString("jsonState"),
                savedInstanceState.getInt(JsonFormConstants.VISUALIZATION_MODE_EXTRA),
                savedInstanceState.getString("resolver"), savedInstanceState.getString("resourceResolver"));
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
                Log.e(TAG, e.getMessage(), e);
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
                String keyAtIndex = item.optString("key");
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
                            if (value != null && !"".equals(value)) {
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
        if (mJSONObject != null) {
            outState.putString("jsonState", mJSONObject.toString());
            outState.putInt(JsonFormConstants.VISUALIZATION_MODE_EXTRA, mVisualizationMode);
            outState.putString("resolver", externalContentResolverClass);
            outState.putString("resourceResolver", resourceResolverClass);
        }
    }

    @Override
    public int getVisualizationMode() {
        return mVisualizationMode;
    }

    @Override
    public JsonFormBundle getBundle(Locale locale) {
        if (mJsonBundle == null) {
            try {
                mJsonBundle = new JsonFormBundle(mJSONObject, locale);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return mJsonBundle;
    }

    @Override
    public JsonExpressionResolver getExpressionResolver() {
        if (mResolver == null) {
            try {
                if (mContentResolver != null) {
                    mResolver = new JsonExpressionResolver(mJSONObject, mContentResolver);
                } else {
                    mResolver = new JsonExpressionResolver(mJSONObject);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return mResolver;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return mResourceResolver;
    }

    @Override
    public ExternalContentResolver getExternalContentResolver() {
        return mContentResolver;
    }

    @Override
    public void historyPush(String stepName) throws JSONException {
        synchronized (mJSONObject) {
            JSONArray history = mJSONObject.optJSONArray(PROP_HISTORY);
            if (history == null) {
                history = new JSONArray();
                mJSONObject.put(PROP_HISTORY, history);
            }
            JSONObject slice = new JSONObject();
            slice.put("name", stepName);
            JSONObject stepState = mJSONObject.optJSONObject(stepName);
            if (stepState != null && stepState.has("fields")) {
                slice.put("state", JsonFormUtils.extractDataFromForm(stepState));
            } else {
                slice.put("state", stepState);
            }
            history.put(slice);
        }
    }

    @Override
    public void historyPop() {
        synchronized (mJSONObject) {
            JSONArray history = mJSONObject.optJSONArray(PROP_HISTORY);
            if (history != null) {
                history.remove(history.length() - 1);
            }
        }
    }

    private void configureInputMethod() {
        if (getIntent().hasExtra(JsonFormConstants.INPUT_METHOD_EXTRA)) {
            int inputMethod = getIntent().getIntExtra(JsonFormConstants.INPUT_METHOD_EXTRA, -1);
            if (inputMethod == JsonFormConstants.INPUT_METHOD_VISIBLE) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            } else if (inputMethod == JsonFormConstants.INPUT_METHOD_HIDDEN) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }
        if (JsonFormConstants.VISUALIZATION_MODE_EDIT != mVisualizationMode) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void configureOrientation() {
        Intent intent = getIntent();
        int rotation;
        boolean hasOrientationExtra = intent.hasExtra(JsonFormConstants.ORIENTATION_EXTRA);
        boolean hasCurrentOrientationExtra = intent.hasExtra(JsonFormConstants.CURRENT_ORIENTATION_EXTRA);
        int currentOrientation = getResources().getConfiguration().orientation;

        if (hasCurrentOrientationExtra) {
            rotation = getIntent().getIntExtra(JsonFormConstants.CURRENT_ORIENTATION_EXTRA, currentOrientation);
        } else {
            rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        }
        if (hasOrientationExtra) {
            int orientation = getIntent().getIntExtra(JsonFormConstants.ORIENTATION_EXTRA, currentOrientation);
            if (JsonFormConstants.ORIENTATION_LANDSCAPE == orientation) {
                if (rotation == Surface.ROTATION_90) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (rotation == Surface.ROTATION_270) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } else if (JsonFormConstants.ORIENTATION_PORTRAIT == orientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            getIntent().removeExtra(JsonFormConstants.ORIENTATION_EXTRA);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisualizationMode != JsonFormConstants.VISUALIZATION_MODE_READ_ONLY) {
            Intent intent = new Intent("jsonFormPaused");
            String json = mJSONObject.toString();
            // Avoid sending more than 200Kb as intent extra
            if (json != null && json.length() >= MAX_PARCEL_SIZE) {
                Uri uri = StateProvider.saveState(this, json);
                intent.putExtra("uri", uri);
            } else {
                intent.putExtra("json", json);
            }
            intent.putExtra("pausedStep", PropertiesUtils.getInstance(getBaseContext()).getPausedStep());
            PropertiesUtils.getInstance(getBaseContext()).setPausedStep(null);
            sendBroadcast(intent);
        }
    }
}
