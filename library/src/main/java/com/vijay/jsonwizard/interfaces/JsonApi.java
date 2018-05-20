package com.vijay.jsonwizard.interfaces;

import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by vijay on 5/16/15.
 */
public interface JsonApi {
    JSONObject getStep(String stepName);

    void writeValue(String stepName, String key, String value) throws JSONException;

    void writeValue(String stepName, String prentKey, String childObjectKey, String childKey, String value)
            throws JSONException;

    String currentJsonState();

    String getCount();

    int getVisualizationMode();

    JsonFormBundle getBundle(Locale locale);

    JsonExpressionResolver getExpressionResolver();
}
