package com.vijay.jsonwizard.utils;

import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFormUtils {

    private static final String TAG = "JsonFormUtils";

    public static JSONObject mergeFormData(JSONObject form, JSONObject dataJson)
            throws JSONException {
        JSONObject mergedForm = new JSONObject(form.toString());

        Iterator<String> keys = dataJson.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject formField = findFieldInForm(mergedForm, key);
            if (formField != null) {
                formField.put("value", dataJson.get(key));
            } else {
                Log.d(TAG, key + " NOT FOUND!");
            }
        }

        return mergedForm;
    }


    public static JSONObject extractDataFromForm(JSONObject form)
            throws JSONException {
        Map<String, Object> dataMap = new HashMap<>();

        JSONArray names = form.names();
        for (int i = 0; i < names.length(); i++) {
            String nodeName = names.get(i).toString();
            if (nodeName.contains("step")) {
                processFieldContainer((JSONObject) form.get(nodeName), dataMap);
            }
        }
        return new JSONObject(dataMap);
    }

    private static JSONObject findFieldInForm(JSONObject form, String key) throws JSONException {
        int count = form.getInt("count");
        for (int i = 1; i <= count; i++) {
            JSONObject step = form.getJSONObject("step" + i);
            JSONObject field = findInJSON(step, key);
            if (field != null) {
                return field;
            }
        }
        return null;
    }

    private static JSONObject findInJSON(JSONObject root, String key) throws JSONException {
        JSONArray fields = root.getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = (JSONObject) fields.get(i);
            if (isContainer(field)) {
                field = findInJSON(field, key);
            }
            if (field != null && field.has("key") && field.getString("key").equals(key)) {
                return field;
            }
        }
        return null;
    }

    private static void processFieldContainer(JSONObject container, Map<String, Object> dataMap)
            throws JSONException {
        JSONArray fields = container.optJSONArray("fields");
        if (fields != null) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = (JSONObject) fields.get(i);
                if (isContainer(field)) {
                    processFieldContainer(field, dataMap);
                } else {
                    if (field.has("key") && field.has("value")) {
                        dataMap.put(field.getString("key"), field.get("value"));
                    } else if (isCheckbox(field)) {
                        processCheckbox(field, dataMap);
                    }
                }
            }
        }
    }

    private static void processCheckbox(JSONObject field, Map<String, Object> dataMap)
            throws JSONException {
        JSONArray options = field.optJSONArray("options");
        if (options != null) {
            for (int j = 0; j < options.length(); j++) {
                JSONObject option = (JSONObject) options.get(j);
                if (option.has("key") && option.has("value")) {
                    dataMap.put(option.getString("key"), option.get("value"));
                }
            }
        }
    }

    private static boolean isContainer(JSONObject field) throws JSONException {
        return "edit_group".equals(field.getString("type"));
    }

    private static boolean isCheckbox(JSONObject field) throws JSONException {
        return "check_box".equals(field.getString("type"));
    }
}
