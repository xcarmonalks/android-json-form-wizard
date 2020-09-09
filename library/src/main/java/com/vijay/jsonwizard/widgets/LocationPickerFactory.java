package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.maps.LocationPart;
import com.vijay.jsonwizard.maps.LocationTextWatcher;
import com.vijay.jsonwizard.maps.LocationValueReporter;
import com.vijay.jsonwizard.maps.MapsUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.edittext.EqualsValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationPickerFactory implements FormWidgetFactory {

    public static final String KEY_SUFFIX_LATITUDE = "_latitude";
    public static final String KEY_SUFFIX_LONGITUDE = "_longitude";
    public static final String KEY_SUFFIX_ACCURACY = "_accuracy";

    private static final String TAG = "JsonFormActivity";

    private static final int INPUT_TYPE_DECIMAL_NUMBER = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
            | InputType.TYPE_NUMBER_FLAG_SIGNED;

    public static ValidationStatus validate(MaterialEditText editText) {
        boolean validate = editText.validate();
        if (!validate) {
            return new ValidationStatus(false, editText.getError().toString());
        }
        return new ValidationStatus(true, null);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
        CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
        ResourceResolver resourceResolver, int visualizationMode) throws Exception {
        List<View> views;
        switch (visualizationMode) {
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY:
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle, resourceResolver);
                break;
            default:
                views = getEditableViewsFromJson(stepName, context, jsonObject, listener, bundle,
                    resolver, resourceResolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context,
        JSONObject jsonObject, final CommonListener listener, JsonFormBundle bundle,
        JsonExpressionResolver resolver, ResourceResolver resourceResolver) throws JSONException {

        String readonlyValue = jsonObject.optString("readonly");
        boolean readonly = false;

        if (resolver.isValidExpression(readonlyValue)) {
            JSONObject currentValues = getCurrentValues(context);
            readonly = resolver.existsExpression(readonlyValue, currentValues);
        } else {
            readonly = Boolean.TRUE.toString().equalsIgnoreCase(readonlyValue);
        }

        if (readonly) {
            return getReadOnlyViewsFromJson(context, jsonObject, bundle, resourceResolver);
        }

        String jsonKey = jsonObject.getString("key");
        String jsonInputType = jsonObject.getString("type");

        List<View> views = new ArrayList<>(1);
        final View parentView = LayoutInflater.from(context).inflate(R.layout.item_location_text,
            null);
        parentView.setTag(R.id.key, jsonKey);
        parentView.setTag(R.id.type, jsonInputType);
        boolean accuracyEnabled = jsonObject.has("accuracy") && jsonObject.getBoolean("accuracy");
        parentView.setTag(R.id.accuracy, accuracyEnabled);
        if (jsonObject.has("icon")) {
            String customIcon = jsonObject.getString("icon");
            customIcon = resourceResolver.resolvePath(context, customIcon);
            parentView.setTag(R.id.custom_icon, customIcon);
        }

        View mapContainer = parentView.findViewById(R.id.map_container);
        mapContainer.setId(View.generateViewId());
        mapContainer.setTag(R.id.map_container);

        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        final TextView label = parentView.findViewById(R.id.location_label);
        label.setText(hint);

        View inputContainer = parentView.findViewById(R.id.value_container);
        inputContainer.setTag(R.id.key, jsonKey);
        inputContainer.setTag(R.id.type, jsonInputType);

        MaterialEditText etLatitude = parentView.findViewById(R.id.location_latitude);
        etLatitude.setId(ViewUtil.generateViewId());
        etLatitude.setInputType(INPUT_TYPE_DECIMAL_NUMBER);
        etLatitude.setTag(R.id.key, jsonKey + KEY_SUFFIX_LATITUDE);
        etLatitude.setTag(R.id.type, jsonInputType);

        MaterialEditText etLongitude = parentView.findViewById(R.id.location_longitude);
        etLongitude.setId(ViewUtil.generateViewId());
        etLongitude.setInputType(INPUT_TYPE_DECIMAL_NUMBER);
        etLongitude.setTag(R.id.key, jsonKey + KEY_SUFFIX_LONGITUDE);
        etLongitude.setTag(R.id.type, jsonInputType);

        MaterialEditText etAccuracy = parentView.findViewById(R.id.location_accuracy);
        etAccuracy.setId(ViewUtil.generateViewId());
        etAccuracy.setInputType(INPUT_TYPE_DECIMAL_NUMBER);
        etAccuracy.setTag(R.id.key, jsonKey + KEY_SUFFIX_ACCURACY);
        etAccuracy.setTag(R.id.type, jsonInputType);

        if (accuracyEnabled) {
            etAccuracy.setVisibility(View.VISIBLE);
        } else {
            etAccuracy.setVisibility(View.GONE);
        }

        final ImageView imageView = parentView.findViewById(R.id.icon);
        String key = jsonKey;
        imageView.setTag(R.id.key, jsonKey);
        imageView.setTag(R.id.type, jsonInputType);
        final View.OnClickListener onClickListenerWithValue = getOnClickListenerWithValue(
            parentView, etLatitude, etLongitude, etAccuracy, listener, accuracyEnabled);
        imageView.setOnClickListener(onClickListenerWithValue);
        mapContainer.setOnClickListener(onClickListenerWithValue);

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            String customIcon = null;
            if (jsonObject.has("icon")) {
                customIcon = jsonObject.getString("icon");
                customIcon = resourceResolver.resolvePath(context, customIcon);
            }
            fillDefaultValue(context, etLatitude, etLongitude, etAccuracy, mapContainer.getId(), key, value, customIcon);
        }

        //add validators
        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                boolean required = false;
                if (resolver.isValidExpression(requiredValue)) {
                    JSONObject currentValues = getCurrentValues(context);
                    required = resolver.existsExpression(requiredValue, currentValues);
                } else {
                    required = Boolean.TRUE.toString().equalsIgnoreCase(requiredValue);
                }

                if (required) {
                    etLatitude.addValidator(
                        new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                    etLongitude.addValidator(
                            new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                    if (accuracyEnabled) {
                        etAccuracy.addValidator(
                                new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                    }
                }
            }
        }

        JSONObject accuracyValidator = jsonObject.optJSONObject("v_exact_position");
        if (accuracyEnabled && accuracyValidator != null) {
            etAccuracy.addValidator(
                    new EqualsValidator("-1", bundle.resolveKey(accuracyValidator.getString("err")), true));
        }

        LocationValueReporter valueReporter;
        if (TextUtils.isEmpty(value)) {
            valueReporter = new LocationValueReporter(stepName, parentView, accuracyEnabled);
        } else {
            valueReporter = new LocationValueReporter(stepName, parentView, value, accuracyEnabled);
        }
        etLatitude.addTextChangedListener(new LocationTextWatcher(LocationPart.LATITUDE, valueReporter));
        etLongitude.addTextChangedListener(new LocationTextWatcher(LocationPart.LONGITUDE, valueReporter));
        if (accuracyEnabled) {
            etAccuracy.addTextChangedListener(new LocationTextWatcher(LocationPart.ACCURACY, valueReporter));
        }
        views.add(parentView);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject,
                                                JsonFormBundle bundle, ResourceResolver resourceResolver) throws JSONException {
        List<View> views = new ArrayList<>(1);
        View parentView = LayoutInflater.from(context).inflate(R.layout.item_location_text, null);

        View mapContainer = parentView.findViewById(R.id.map_container);
        mapContainer.setId(View.generateViewId());
        mapContainer.setTag(R.id.map_container);

        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        final TextView label = parentView.findViewById(R.id.location_label);
        label.setText(hint);

        String jsonKey = jsonObject.getString("key");
        String jsonType = jsonObject.getString("type");
        final MaterialEditText etLatitude = parentView.findViewById(R.id.location_latitude);
        etLatitude.setId(ViewUtil.generateViewId());
        etLatitude.setTag(R.id.key, jsonKey + KEY_SUFFIX_LATITUDE);
        etLatitude.setTag(R.id.type, jsonType);
        etLatitude.setEnabled(false);
        final MaterialEditText etLongitude = parentView.findViewById(R.id.location_longitude);
        etLongitude.setId(ViewUtil.generateViewId());
        etLongitude.setTag(R.id.key, jsonKey + KEY_SUFFIX_LONGITUDE);
        etLongitude.setTag(R.id.type, jsonType);
        etLongitude.setEnabled(false);

        final MaterialEditText etAccuracy = parentView.findViewById(R.id.location_accuracy);
        etAccuracy.setId(ViewUtil.generateViewId());
        etAccuracy.setTag(R.id.key, jsonKey + KEY_SUFFIX_ACCURACY);
        etAccuracy.setTag(R.id.type, jsonType);
        etAccuracy.setEnabled(false);
        boolean accuracyEnabled = jsonObject.has("accuracy") && jsonObject.getBoolean("accuracy");
        if (accuracyEnabled) {
            etAccuracy.setVisibility(View.VISIBLE);
        }

        String value = jsonObject.optString("value");

        if (!TextUtils.isEmpty(value)) {
            String customIcon = null;
            if (jsonObject.has("icon")) {
                customIcon = jsonObject.getString("icon");
                customIcon = resourceResolver.resolvePath(context, customIcon);
            }
            fillDefaultValue(context, etLatitude, etLongitude, etAccuracy, mapContainer.getId(), jsonKey, value, customIcon);
        } else {
            mapContainer.setVisibility(View.GONE);
        }

        final ImageView imageView = parentView.findViewById(R.id.icon);
        imageView.setClickable(false);
        imageView.setEnabled(false);
        imageView.setVisibility(View.GONE);

        views.add(parentView);
        return views;
    }


    private void fillDefaultValue(Context context, MaterialEditText etLatitude, MaterialEditText etLongitude,
                                  MaterialEditText etAccuracy, int mapContainerId, String key, String value, String customIcon) {
        String[] parts = value.split(MapsUtils.COORD_SEPARATOR);
        if (parts.length > 0) {
            etLatitude.setText(parts[0].trim());
        }
        if (parts.length > 1) {
            etLongitude.setText(parts[1].trim());
        }
        if (parts.length > 2) {
            etAccuracy.setText(parts[2].trim());
        }
        if (context instanceof FragmentActivity) {
            MapsUtils.loadStaticMap((FragmentActivity) context, mapContainerId, key, value, customIcon);
        } else {
            Log.w(TAG, "Context is not a FragmentActivity");
        }
    }

    private View.OnClickListener getOnClickListenerWithValue(final View parentView, final EditText etLatitude,
                                                             final EditText etLongitude, final EditText etAccuracy,
                                                             final CommonListener listener, final boolean accuracy) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value;
                if (accuracy) {
                    value = MapsUtils.toString(etLatitude.getText().toString(),
                            etLongitude.getText().toString(),
                            etAccuracy.getText().toString());
                } else {
                    value = MapsUtils.toString(etLatitude.getText().toString(),
                            etLongitude.getText().toString());
                }
                parentView.setTag(R.id.value, value);
                listener.onClick(parentView);
            }
        };
    }

    @Nullable
    private JSONObject getCurrentValues(Context context) throws JSONException {
        JSONObject currentValues = null;
        if (context instanceof JsonApi) {
            String currentJsonState = ((JsonApi) context).currentJsonState();
            JSONObject currentJsonObject = new JSONObject(currentJsonState);
            currentValues = JsonFormUtils.extractDataFromForm(currentJsonObject, false);
        }
        return currentValues;
    }




}
