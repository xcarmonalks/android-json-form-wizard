package com.vijay.jsonwizard.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONException;
import org.json.JSONObject;

public class ExpressionResolverContextUtils {
    private static final String TAG = "ExpContextUtils";

    @Nullable
    public static JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        JSONObject currentValues = null;
        if (context instanceof JsonApi) {
            String currentJsonState = ((JsonApi) context).currentJsonState();
            JSONObject currentJsonObject = new JSONObject(currentJsonState);
            currentValues = JsonFormUtils.extractDataFromForm(currentJsonObject, false);

            JSONObject step = currentJsonObject.getJSONObject(stepName);
            if (step.has("template_params")) {
                /*
                  TODO: Optimize step retrieval. Following method rebuilds step from template.
                  Can be optimized by giving access to the step definition built during fragment initialization
                */
                step = ((JsonApi) context).getStep(stepName);
                JSONObject templateParams = step.getJSONObject("template_params");
                currentValues.put("template_params", templateParams);
            }
        }
        return currentValues;
    }
}
