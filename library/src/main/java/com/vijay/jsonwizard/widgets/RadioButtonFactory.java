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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialTextInputLayout;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.ExpressionResolverContextUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class RadioButtonFactory implements FormWidgetFactory {

    private final String H_ORIENTATION_VALUE = "horizontal";

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

        String orientationStr = (String) jsonObject.get("orientation");
        boolean horizontal = H_ORIENTATION_VALUE.equals(orientationStr);
        int layoutOrientation = horizontal ? RadioGroup.HORIZONTAL : RadioGroup.VERTICAL;
        int layoutWidth = horizontal ? WRAP_CONTENT : MATCH_PARENT;

        JSONArray options = null;
        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);
        if (valuesExpression != null) {
            JSONObject currentValues = getCurrentValues(context, stepName);
            options = resolver.resolveAsArray(valuesExpression, currentValues);
        } else {
            options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        }

        int optionsLength = options.length();
        if (optionsLength > 0) {
            RadioGroup rg = new RadioGroup(context);
            rg.setOrientation(layoutOrientation);
            final String value = jsonObject.optString("value");
            String resolvedValue;
            if (resolver.isValidExpression(value)) {
                resolvedValue = resolver.resolveAsString(value, getCurrentValues(context, stepName));
                if (resolvedValue == null) {
                    resolvedValue = "";
                }
            } else {
                resolvedValue = value;
            }
            for (int i = 0; i < optionsLength; i++) {
                JSONObject item = options.getJSONObject(i);
                RadioButton radioButton = (RadioButton) LayoutInflater.from(context).inflate(R.layout.item_radiobutton,
                    null);
                radioButton.setId(i);
                radioButton.setText(bundle.resolveKey(item.getString("text")));
                radioButton.setTag(R.id.key, jsonObject.getString("key"));
                radioButton.setTag(R.id.type, jsonObject.getString("type"));
                radioButton.setTag(R.id.childKey, item.getString("key"));
                radioButton.setGravity(Gravity.CENTER_VERTICAL);
                radioButton.setTextSize(16);
                radioButton.setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_REGULAR_PATH));
                radioButton.setOnCheckedChangeListener(listener);
                if (!TextUtils.isEmpty(resolvedValue) && resolvedValue.equals(item.getString("key"))) {
                    radioButton.setChecked(true);
                }
                radioButton.setLayoutParams(getLayoutParams(layoutWidth, WRAP_CONTENT, 0, 0, 0,
                    (int) context.getResources().getDimension(R.dimen.extra_bottom_margin)));
                rg.addView(radioButton);
            }
            views.add(rg);
        }
        return views;
    }

    private String getValuesAsJsonExpression(JSONObject jsonObject, JsonExpressionResolver resolver) {
        String valuesExpression = jsonObject.optString(JsonFormConstants.OPTIONS_FIELD_NAME);
        if (resolver.isValidExpression(valuesExpression)) {
            return valuesExpression;
        }
        return null;
    }

    @Nullable
    private JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        return ExpressionResolverContextUtils.getCurrentValues(context, stepName);
    }


    private List<View> getReadOnlyViewsFromJson(String stepName, Context context, JSONObject jsonObject, JsonFormBundle bundle, JsonExpressionResolver resolver)
        throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialTextInputLayout materialTextInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(R.layout.item_material_edit_text,
            null);
        EditText editText = materialTextInputLayout.getEditText();

        editText.setId(ViewUtil.generateViewId());
        final String label = bundle.resolveKey(jsonObject.getString("label"));
        materialTextInputLayout.setHint(label);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        String value = jsonObject.optString("value");
        String resolvedValue;
        if (resolver.isValidExpression(value)) {
            resolvedValue = resolver.resolveAsString(value, getCurrentValues(context, stepName));
            if (resolvedValue == null) {
                resolvedValue = "";
            }
        } else {
            resolvedValue = value;
        }
        editText.setText(resolveValueText(resolvedValue, jsonObject, bundle));
        editText.setEnabled(false);
        materialTextInputLayout.setHintTextColor(materialTextInputLayout.getCounterTextColor());
        views.add(materialTextInputLayout);
        return views;
    }

    private String resolveValueText(String value, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        String valueText = "";
        if (value != null && !"".equals(value)) {
            JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int i = 0; i < options.length(); i++) {
                JSONObject item = options.getJSONObject(i);
                if (value.equals(item.optString("key"))) {
                    valueText = bundle.resolveKey(item.optString("text"));
                    break;
                }
            }
        }
        return valueText;
    }
}
