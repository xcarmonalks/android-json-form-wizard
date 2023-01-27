package com.vijay.jsonwizard.interactors;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.ExpressionResolverContextUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.widgets.WidgetFactoryRegistry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 5/19/15.
 */
public class JsonFormInteractor {

    private static final String TAG = "JsonFormInteractor";
    private static final JsonFormInteractor INSTANCE = new JsonFormInteractor();

    private JsonFormInteractor() {
    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }

    public List<View> fetchFormElements(String stepName, Context context, JSONObject parentJson,
        CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
        ResourceResolver resourceResolver, int visualizationMode) {
        Log.d(TAG, "fetchFormElements called");
        List<View> viewsFromJson = new ArrayList<>(5);
        try {
            JSONArray fields = parentJson.getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject childJson = fields.getJSONObject(i);
                if (isVisible(stepName,childJson, context, resolver)) {
                    try {
                        List<View> views = WidgetFactoryRegistry.getWidgetFactory(childJson.getString("type")).
                            getViewsFromJson(stepName, context, childJson, listener, bundle, resolver, resourceResolver,
                                visualizationMode);
                        if (views.size() > 0) {
                            viewsFromJson.addAll(views);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception occurred in making child view at index : " + i + " : Exception is : " + e
                            .getMessage(), e);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json exception occurred : " + e.getMessage(), e);
        }
        return viewsFromJson;
    }

    private boolean isVisible(String stepName, JSONObject jsonObject, Context context, JsonExpressionResolver resolver) {

        final String showExpression = jsonObject.optString("show");
        if (!TextUtils.isEmpty(showExpression)) {
            if (resolver.isValidExpression(showExpression)) {
                try {
                    JSONObject currentValues = ExpressionResolverContextUtils.getCurrentValues(context,stepName);
                    return resolver.existsExpression(showExpression, currentValues);
                } catch (JSONException e) {
                    Log.e(TAG, "isVisible: Error evaluating expression " + showExpression, e);
                }
                return false;
            }

            return ("true".equalsIgnoreCase(showExpression));
        }

        final String hideExpression = jsonObject.optString("hide");
        if (!TextUtils.isEmpty(hideExpression)) {
            if (resolver.isValidExpression(hideExpression)) {
                try {
                    JSONObject currentValues = ExpressionResolverContextUtils.getCurrentValues(context,stepName);
                    return !resolver.existsExpression(hideExpression, currentValues);
                } catch (JSONException e) {
                    Log.e(TAG, "isVisible: Error evaluating expression " + showExpression, e);
                }
                return true;
            }

            return !("true".equalsIgnoreCase(hideExpression));
        }

        return true;
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
