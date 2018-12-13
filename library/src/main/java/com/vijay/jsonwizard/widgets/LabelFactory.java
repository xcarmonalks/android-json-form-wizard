package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.FONT_REGULAR_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

/**
 * Created by vijay on 24-05-2015.
 */
public class LabelFactory implements FormWidgetFactory {

    private static final String KEY_FIELD = "key";
    private static final String TEXT_FIELD = "text";
    private static final String TYPE_FIELD = "type";
    private static final String HINT_FIELD = "hint";
    private static final String BOLD_FIELD = "bold";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
        if (TextUtils.isEmpty(jsonObject.optString(HINT_FIELD))) {
            return getAsLabel(stepName, context, jsonObject, listener, bundle, resolver,
                    visualizationMode);
        } else {
            return getAsReadOnlyEditText(stepName, context, jsonObject, listener, bundle, resolver,
                    visualizationMode);
        }
    }

    private List<View> getAsLabel(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode)  throws JSONException {
        List<View> views = new ArrayList<>(1);
        LinearLayout.LayoutParams layoutParams = getLayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0, 0 , 0, (int) context
                .getResources().getDimension(R.dimen.default_bottom_margin));

        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);

        String textValue = null;
        if (valuesExpression == null) {
            textValue = bundle.resolveKey(jsonObject.getString(TEXT_FIELD));
        } else {
            JSONObject currentValues = getCurrentValues(context);
            textValue = resolver.resolveAsString(valuesExpression,currentValues);
        }

        boolean bold = jsonObject.optBoolean(BOLD_FIELD,true);

        views.add(getTextViewWith(context, 16,Html.fromHtml(textValue), jsonObject.getString(KEY_FIELD),
                jsonObject.getString(TYPE_FIELD), layoutParams, bold?FONT_BOLD_PATH:FONT_REGULAR_PATH));

        return views;
    }

    private List<View> getAsReadOnlyEditText(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
        List<View> views = new ArrayList<>(1);

        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setId(ViewUtil.generateViewId());
        final String hint = bundle.resolveKey(jsonObject.getString(HINT_FIELD));
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setTag(R.id.key, jsonObject.getString(KEY_FIELD));
        editText.setTag(R.id.type, jsonObject.getString(TYPE_FIELD));

        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);

        String textValue = null;
        if (valuesExpression == null) {
            textValue = bundle.resolveKey(jsonObject.getString(TEXT_FIELD));
        } else {
            JSONObject currentValues = getCurrentValues(context);
            textValue = resolver.resolveAsString(valuesExpression,currentValues);
        }

        editText.setText(Html.fromHtml(textValue));
        editText.setEnabled(false);
        views.add(editText);
        return views;

    }

    @Nullable
    private JSONObject getCurrentValues(Context context) throws JSONException {
        JSONObject currentValues = null;
        if (context instanceof JsonApi) {
            String currentJsonState = ((JsonApi) context).currentJsonState();
            JSONObject currentJsonObject = new JSONObject(currentJsonState);
            currentValues =  JsonFormUtils.extractDataFromForm(currentJsonObject,false);
        }
        return currentValues;
    }

    private String getValuesAsJsonExpression(JSONObject jsonObject, JsonExpressionResolver resolver) {
        String valuesExpression = jsonObject.optString(TEXT_FIELD);
        if (resolver.isValidExpression(valuesExpression)) {
            return valuesExpression;
        }
        return null;
    }


}
