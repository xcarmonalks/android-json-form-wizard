package com.vijay.jsonwizard.i18n;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jurkiri on 12/12/17.
 */
public class JsonFormBundle {

    private static final String BUNDLE_KEY_PREFIX = "${";
    private static final String BUNDLE_KEY_SUFIX = "}";

    private final Map<String, String> mBundle;

    public JsonFormBundle(JSONObject form, Locale locale) throws JSONException {
        mBundle = new HashMap<>();
        if(form.has("bundle")){
            loadBundle(form.getJSONObject("bundle"), locale.getLanguage());
        }
    }

    public String resolveKey(String key) {
        if(!TextUtils.isEmpty(key) && key.startsWith(BUNDLE_KEY_PREFIX) && key.endsWith(BUNDLE_KEY_SUFIX)){
            final String finalKey = key.substring(2, key.length() - 1);
            if(mBundle.containsKey(finalKey)) {
                return mBundle.get(finalKey);
            }
        }
        return key;
    }

    private void loadBundle(JSONObject bundle, String country) throws JSONException {
        if(bundle.has(country)){
            JSONObject translations = bundle.getJSONObject(country);
            Iterator<String> transIter = translations.keys();
            while(transIter.hasNext()){
                String key = transIter.next();
                mBundle.put(key, translations.getString(key));
            }
        }
    }
}
