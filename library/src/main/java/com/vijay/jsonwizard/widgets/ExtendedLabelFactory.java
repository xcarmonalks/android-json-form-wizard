package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;


import com.vijay.jsonwizard.R;
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
import org.sufficientlysecure.htmltextview.HtmlAssetsImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class ExtendedLabelFactory implements FormWidgetFactory {

    private static final String TEXT_FIELD = "text";
    private static final String PARAMS_FIELD = "params";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                       JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver,
                                       int visualizationMode) throws JSONException {
        return getAsLabel(stepName, context, jsonObject, listener, bundle, resolver, visualizationMode, resourceResolver);
    }

    private List<View> getAsLabel(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                  JsonFormBundle bundle, JsonExpressionResolver resolver, int visualizationMode,
                                  ResourceResolver resourceResolver) throws JSONException {
        List<View> views = new ArrayList<>(1);

        HtmlTextView textView = (HtmlTextView) LayoutInflater.from(context).inflate(R.layout.item_extended_label, null);
        textView.setId(View.generateViewId());


        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);

        String textValue = null;
        JSONObject currentValues = null;
        if (valuesExpression == null) {
            textValue = bundle.resolveKey(jsonObject.getString(TEXT_FIELD));
        } else {
            currentValues = getCurrentValues(context, stepName);
            textValue = resolver.resolveAsString(valuesExpression, currentValues);
        }

        List<String> paramValues = new ArrayList<>();
        JSONArray params = jsonObject.optJSONArray(PARAMS_FIELD);
        if (params != null && params.length() > 0) {
            if (currentValues == null) {
                currentValues = getCurrentValues(context, stepName);
            }
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

        textView.setHtml(textValue, new HtmlResourceImageGetter(context, resourceResolver));
        views.add(textView);
        return views;
    }

    @Nullable
    private JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        return ExpressionResolverContextUtils.getCurrentValues(context, stepName);
    }

    private String getValuesAsJsonExpression(JSONObject jsonObject, JsonExpressionResolver resolver) {
        String valuesExpression = jsonObject.optString(TEXT_FIELD);
        if (resolver.isValidExpression(valuesExpression)) {
            return valuesExpression;
        }
        return null;
    }


    private class HtmlResourceImageGetter extends HtmlAssetsImageGetter {

        private static final String TAG = "JsonFormsActivity";

        private final Context context;
        private final ResourceResolver resolver;

        public HtmlResourceImageGetter(Context context, ResourceResolver resolver) {
            super(context);
            this.context = context;
            this.resolver = resolver;
        }

        @Override
        public Drawable getDrawable(String source) {
            Drawable d = super.getDrawable(source);
            if (d == null) {
                // Use resource resolver
                String imagePath = resolver.resolvePath(context, source);
                d = Drawable.createFromPath(imagePath);
                if (d == null) {
                    Log.e(TAG, "source could not be found by resource resolver: " + source);
                    return null;
                }
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            }
            return d;
        }
    }
}
