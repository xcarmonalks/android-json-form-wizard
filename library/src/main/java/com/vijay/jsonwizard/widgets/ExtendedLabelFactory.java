package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlAssetsImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * Created by vijay on 24-05-2015.
 */
public class ExtendedLabelFactory implements FormWidgetFactory {

    private static final String TEXT_FIELD = "text";
    private static final String PARAMS_FIELD = "hint";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
            CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
            int visualizationMode) throws JSONException {
        return getAsLabel(stepName, context, jsonObject, listener, bundle, resolver,
                visualizationMode);
    }

    private List<View> getAsLabel(String stepName, Context context, JSONObject jsonObject,
            CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
            int visualizationMode) throws JSONException {
        List<View> views = new ArrayList<>(1);

        HtmlTextView textView = (HtmlTextView) LayoutInflater.from(context)
                .inflate(R.layout.item_extended_label, null);
        textView.setId(ViewUtil.generateViewId());


        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);

        String textValue = null;
        if (valuesExpression == null) {
            textValue = bundle.resolveKey(jsonObject.getString(TEXT_FIELD));
        } else {
            JSONObject currentValues = getCurrentValues(context);
            textValue = resolver.resolveAsString(valuesExpression, currentValues);
        }

        List<String> paramValues = new ArrayList<>();
        JSONArray params = jsonObject.optJSONArray(PARAMS_FIELD);
        if (params != null && params.length() > 0) {
            JSONObject currentValues = getCurrentValues(context);
            for (int i = 0; i < params.length(); i++) {
                String expression = params.getString(i);
                String value = "";
                if (resolver.isValidExpression(expression)) {
                    value = resolver.resolveAsString(expression, currentValues);
                }
                paramValues.add(value);
            }
            try {
                textValue = MessageFormat.format(textValue, paramValues.toArray());
            } catch (Exception e) {
                Log.e("ExtendedLabelFactory", "getAsLabel: Error formating message", e);
            }
        }

        textView.setHtml(textValue, new HtmlAssetsImageGetter(textView));
        views.add(textView);
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

    private String getValuesAsJsonExpression(JSONObject jsonObject,
            JsonExpressionResolver resolver) {
        String valuesExpression = jsonObject.optString(TEXT_FIELD);
        if (resolver.isValidExpression(valuesExpression)) {
            return valuesExpression;
        }
        return null;
    }


}
