package com.vijay.jsonwizard.utils;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFormUtils {

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
                        processCheckbox(field,dataMap);
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
