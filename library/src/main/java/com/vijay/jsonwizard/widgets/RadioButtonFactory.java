package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.*;

/**
 * Created by vijay on 24-05-2015.
 */
public class RadioButtonFactory implements FormWidgetFactory {

    private final String H_ORIENTATION_VALUE = "horizontal";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode){
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY :
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(context, jsonObject, listener, bundle,resolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver) throws JSONException {
        List<View> views = new ArrayList<>(1);
        views.add(getTextViewWith(context, 16, bundle.resolveKey(jsonObject.getString("label")), jsonObject.getString("key"),
                jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0),
                FONT_BOLD_PATH));

        String orientationStr = (String) jsonObject.get("orientation");
        boolean horizontal = H_ORIENTATION_VALUE.equals(orientationStr);
        int layoutOrientation = horizontal ? RadioGroup.HORIZONTAL : RadioGroup.VERTICAL;
        int layoutWidth = horizontal ? WRAP_CONTENT : MATCH_PARENT;

        JSONArray options = null;
        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);
        if (valuesExpression!=null) {
            options = resolver.resolveAsArray(valuesExpression);
        } else {
            options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        }

        int optionsLength = options.length();
        if(optionsLength > 0) {
            RadioGroup rg = new RadioGroup(context);
            rg.setOrientation(layoutOrientation);
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
                if (!TextUtils.isEmpty(jsonObject.optString("value"))
                        && jsonObject.optString("value").equals(item.getString("key"))) {
                    radioButton.setChecked(true);
                }
                radioButton.setLayoutParams(getLayoutParams(layoutWidth, WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
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


    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle)  throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setId(ViewUtil.generateViewId());
        final String label = bundle.resolveKey(jsonObject.getString("label"));
        editText.setHint(label);
        editText.setFloatingLabelText(label);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        String value = jsonObject.optString("value");
        editText.setText(resolveValueText(value, jsonObject, bundle));
        editText.setEnabled(false);
        views.add(editText);
        return views;
    }

    private String resolveValueText(String value, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        String valueText = "";
        if (value != null && !"".equals(value)){
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
