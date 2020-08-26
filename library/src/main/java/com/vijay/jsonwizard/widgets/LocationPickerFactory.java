package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.maps.MapsUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationPickerFactory implements FormWidgetFactory {

    private static final String TAG = "JsonFormActivity";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
        CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
        ResourceResolver resourceResolver, int visualizationMode) throws Exception {
        List<View> views;
        switch (visualizationMode) {
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY:
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(stepName, context, jsonObject, listener, bundle,
                    resolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context,
        JSONObject jsonObject, final CommonListener listener, JsonFormBundle bundle,
        JsonExpressionResolver resolver) throws JSONException {

        String readonlyValue = jsonObject.optString("readonly");
        boolean readonly = false;

        if (resolver.isValidExpression(readonlyValue)) {
            JSONObject currentValues = getCurrentValues(context);
            readonly = resolver.existsExpression(readonlyValue, currentValues);
        } else {
            readonly = Boolean.TRUE.toString().equalsIgnoreCase(readonlyValue);
        }

        if (readonly) {
            return getReadOnlyViewsFromJson(context, jsonObject, bundle);
        }

        List<View> views = new ArrayList<>(1);
        final View parentView = LayoutInflater.from(context).inflate(R.layout.item_location_text, null);
        parentView.setTag(R.id.key, jsonObject.getString("key"));
        parentView.setTag(R.id.type, jsonObject.getString("type"));

        final MaterialEditText editText = parentView.findViewById(R.id.edit_text);
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setId(ViewUtil.generateViewId());

        final ImageView imageView = parentView.findViewById(R.id.icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.setTag(R.id.value, editText.getText().toString());
                listener.onClick(parentView);
            }
        });
        String key = jsonObject.getString("key");
        imageView.setTag(R.id.key, jsonObject.getString("key"));
        imageView.setTag(R.id.type, jsonObject.getString("type"));

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            editText.setText(value);
            if (context instanceof FragmentActivity) {
                MapsUtils.loadStaticMap((FragmentActivity) context, key, value);
            } else {
                Log.w(TAG, "Context is not a FragmentActivity");
            }
        }

        if (!TextUtils.isEmpty(jsonObject.optString("lines"))) {
            editText.setSingleLine(false);
            editText.setLines(jsonObject.optInt("lines"));
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
                    editText.addValidator(
                        new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                }
            }
        }

        // edit type check
        String editType = jsonObject.optString("edit_type");
        if (!TextUtils.isEmpty(editType)) {
            if (editType.equals("number")) {
                editText.setRawInputType(
                    InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        }

        editText.addTextChangedListener(new GenericTextWatcher(stepName, editText));
        views.add(parentView);
        return views;
    }



    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject,
        JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        View parentView = LayoutInflater.from(context).inflate(R.layout.item_location_text, null);
        final MaterialEditText editText = parentView.findViewById(R.id.edit_text);
        editText.setId(ViewUtil.generateViewId());
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        editText.setText(jsonObject.optString("value"));

        if (!TextUtils.isEmpty(jsonObject.optString("lines"))) {
            editText.setSingleLine(false);
            editText.setLines(jsonObject.optInt("lines"));
        }
        editText.setEnabled(false);

        final ImageView imageView = parentView.findViewById(R.id.icon);
        imageView.setClickable(false);
        imageView.setEnabled(false);
        imageView.setVisibility(View.GONE);

        views.add(parentView);
        return views;
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
