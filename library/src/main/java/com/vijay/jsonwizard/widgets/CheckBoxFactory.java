package com.vijay.jsonwizard.widgets;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.FONT_REGULAR_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.ExpressionResolverContextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class CheckBoxFactory implements FormWidgetFactory {

    private static final String TAG = "CheckBoxFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                       JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver,
                                       int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode) {
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY:
                views = getReadOnlyViewsFromJson(stepName, context, jsonObject, bundle, resolver);
                break;
            default:
                views = getEditableViewsFromJson(stepName, context, jsonObject, listener, bundle, resolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                                JsonFormBundle bundle, JsonExpressionResolver resolver) throws JSONException {
        List<View> views = new ArrayList<>(1);
        views.add(
                getTextViewWith(context, 16, bundle.resolveKey(jsonObject.getString("label")), jsonObject.getString("key"),
                        jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH));
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            if (isVisible(stepName, item, context, resolver)) {
                MaterialCheckBox checkBox = (MaterialCheckBox) LayoutInflater.from(context).inflate(R.layout.item_checkbox, null);
                checkBox.setText(bundle.resolveKey(item.getString("text")));
                checkBox.setTag(R.id.key, jsonObject.getString("key"));
                checkBox.setTag(R.id.type, jsonObject.getString("type"));
                checkBox.setTag(R.id.childKey, item.getString("key"));
                checkBox.setGravity(Gravity.CENTER_VERTICAL);
                checkBox.setTextSize(16);
                checkBox.setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_REGULAR_PATH));
                checkBox.setOnCheckedChangeListener(listener);
                final String value = item.optString("value");
                if (!TextUtils.isEmpty(value)) {
                    boolean checked;
                    if (resolver.isValidExpression(value)) {
                        checked = resolver.existsExpression(value, getCurrentValues(context, stepName));
                    } else {
                        checked = item.optBoolean("value");
                    }
                    checkBox.setChecked(checked);
                }
                if (i == options.length() - 1) {
                    checkBox.setLayoutParams(getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0,
                            (int) context.getResources().getDimension(R.dimen.extra_bottom_margin)));
                }
                views.add(checkBox);
            }
        }
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(String stepName, Context context, JSONObject jsonObject, JsonFormBundle bundle,
                                                JsonExpressionResolver resolver) throws JSONException {
        List<View> views = new ArrayList<>(1);
        views.add(
                getTextViewWith(context, 16, bundle.resolveKey(jsonObject.getString("label")), jsonObject.getString("key"),
                        jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0,
                                (int) context.getResources().getDimension(R.dimen.extra_bottom_margin)), FONT_BOLD_PATH));
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            if (isVisible(stepName, item, context, resolver)) {
                final String value = item.optString("value");
                if (!TextUtils.isEmpty(value)) {
                    boolean checked;
                    if (resolver.isValidExpression(value)) {
                        checked = resolver.existsExpression(value, getCurrentValues(context, stepName));
                    } else {
                        checked = item.optBoolean("value");
                    }
                    if (checked) {
                        views.add(
                                getTextViewWith(context, 16, bundle.resolveKey(item.getString("text")), item.getString("key"),
                                        null, getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0,
                                                (int) context.getResources().getDimension(R.dimen.default_bottom_margin)),
                                        FONT_REGULAR_PATH));
                    }
                }
            }
        }
        return views;
    }

    private boolean isVisible(String stepName, JSONObject jsonObject, Context context, JsonExpressionResolver resolver) {

        final String showExpression = jsonObject.optString("show");
        if (TextUtils.isEmpty(showExpression)) {
            return true;
        }
        if (resolver.isValidExpression(showExpression)) {
            try {
                JSONObject currentValues = getCurrentValues(context, stepName);
                return resolver.existsExpression(showExpression, currentValues);
            } catch (JSONException e) {
                Log.e(TAG, "isVisible: Error evaluating expression " + showExpression, e);
            }
            return false;
        }

        return ("true".equalsIgnoreCase(showExpression));
    }

    @Nullable
    private JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        return ExpressionResolverContextUtils.getCurrentValues(context, stepName);
    }
}
