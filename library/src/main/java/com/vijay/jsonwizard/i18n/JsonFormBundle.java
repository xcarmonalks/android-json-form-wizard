package com.vijay.jsonwizard.i18n;

import android.text.TextUtils;
import android.util.Log;

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

    private static final String TAG = "JsonFormBundle";

    private static final String BUNDLE_KEY_PREFIX = "${";
    private static final String BUNDLE_KEY_SUFIX = "}";

    private static final String BUNDLE_DEFAULT_PROPERTY = "default";

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
        String lang = country;
        if(!bundle.has(lang)){
            lang = getDefaultLang(bundle);
        }
        if(!"".equals(lang)){
            JSONObject translations = bundle.getJSONObject(lang);
            Iterator<String> transIter = translations.keys();
            while(transIter.hasNext()){
                String key = transIter.next();
                mBundle.put(key, translations.getString(key));
            }
        }
    }

    private String getDefaultLang(JSONObject bundle) {
        String defLang = "";
        for(int i = 0; i< bundle.names().length(); i++){
            try {
                String currentLang = bundle.names().getString(i);
                if("".equals(defLang)){
                    defLang = currentLang;
                }
                JSONObject translations = bundle.getJSONObject(currentLang);
                if(translations.optBoolean(BUNDLE_DEFAULT_PROPERTY)) {
                    return currentLang;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return defLang;
    }
}
