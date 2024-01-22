package com.vijay.jsonwizard.i18n;

import android.text.TextUtils;
import android.util.Log;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jurkiri on 12/12/17.
 */
public class JsonFormBundle {

    private static final String TAG = "JsonFormBundle";

    private static final String BUNDLE_KEY_PREFIX = "${";
    private static final String BUNDLE_KEY_SUFIX = "}";

    private static final String BUNDLE_DEFAULT_PROPERTY = "default";

    private final Map<String, String> mBundle;
    private boolean twoLevelLocale;


    public JsonFormBundle(JSONObject form, Locale locale) throws JSONException {
        mBundle = new HashMap<>();
        if (form.has("bundle")) {
            JSONObject bundleJson = form.getJSONObject("bundle");
            Map<String, Map<String, String>> bundleHasMap = bundleJson != null ?
                FormUtils.parseChecklistBundle(bundleJson.toString()) :
                null;
            twoLevelLocale = hasTwoLevelLocale(bundleHasMap);
            loadBundleTest(bundleHasMap, getLocale(locale));
        }
    }

    private boolean hasTwoLevelLocale(Map<String, Map<String, String>> bundle) {
        Map.Entry<String, Map<String, String>> entry = bundle.entrySet().iterator().next();
        String key = entry.getKey();
        return key.contains("-");
    }

    private String getLocale(Locale locale) {
        if(twoLevelLocale){
            return locale.getLanguage()+"-"+locale.getCountry();
        }else{
            return locale.getLanguage();
        }
    }

    public String resolveKey(String key) {
        if (!TextUtils.isEmpty(key) && key.startsWith(BUNDLE_KEY_PREFIX) && key.endsWith(BUNDLE_KEY_SUFIX)) {
            final String finalKey = key.substring(2, key.length() - 1);
            if (mBundle.containsKey(finalKey)) {
                return mBundle.get(finalKey);
            }
        }
        return key;
    }

    private void loadBundleTest(Map<String, Map<String, String>> bundle, String deviceLang) {
        String lang = "";
        //Primero revisamos si el bundle contiene nuestro idioma
        if(bundle.containsKey(deviceLang)){
            //El idioma existe en el bundle por lo que cogeremos de el el correspondiente al idioma del dispositivo
            lang = deviceLang;
        }else{
            //El idioma no existe en el bundle, miramos a ver si al menos coincide con los dos primeros caracteres de algún idioma del bundle
            String finalLang = deviceLang;
            List<String>
                filteredBundle =
                bundle.keySet().stream().filter(str -> firstTwoCharacters(str).equals(firstTwoCharacters(finalLang))).collect(
                Collectors.toList());
            if(!filteredBundle.isEmpty()){
                //Hay alguno coincidente nos quedamos con el primero
                lang = filteredBundle.get(0);
            }else{
                //No hay coincidencias, nos quedamos con el idioma que tenga el default, nos quedamos con el primero del bundle aunque no tenga nada que ver con el del disopositivo
                lang = getDefaultLang(bundle);
            }
        }
        Map<String, String> translations = bundle.get(lang);
        if (!"".equals(lang) && translations != null) {
            mBundle.putAll(translations);
        }
    }

    public String firstTwoCharacters(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }


    private String getDefaultLang(Map<String, Map<String, String>> bundle) {
        String defLang = "";
        for (Map.Entry<String, Map<String, String>> lang: bundle.entrySet()) {
            String currentLang = lang.getKey();
            if ("".equals(defLang)) {
                defLang = currentLang;
            }
            Map<String, String> translations = lang.getValue();
            if (translations != null) {
                String isDefault = translations.get(BUNDLE_DEFAULT_PROPERTY);
                if (isDefault != null && !isDefault.equals(Boolean.FALSE.toString())) {
                    return currentLang;
                }
            }
        }
        return defLang;
    }
}
