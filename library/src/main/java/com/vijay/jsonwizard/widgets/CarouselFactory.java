package com.vijay.jsonwizard.widgets;


import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.CarouselAdapter;
import com.vijay.jsonwizard.utils.CarouselItem;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by xcarmona on 21/06/18.
 */
public class CarouselFactory implements FormWidgetFactory {

    private static final String TAG = "CarouselFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode){
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY :
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(context, jsonObject, listener, bundle,resolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver) throws JSONException {
        List<View> views = new ArrayList<>(1);

        DiscreteScrollView scrollView = (DiscreteScrollView) LayoutInflater.from(context).inflate(R.layout.item_carousel, null);

        scrollView.setId(ViewUtil.generateViewId());

        scrollView.setTag(R.id.key, jsonObject.getString("key"));
        scrollView.setTag(R.id.type, jsonObject.getString("type"));

        //Skip required part. One is always selected

        String valueToSelect = "";
        int indexToSelect = -1;
        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            valueToSelect = value;
        }

        String valuesExpression = getValuesAsJsonExpression(jsonObject, resolver);

        JSONArray valuesJson = null;
        if (valuesExpression == null) {
            valuesJson = jsonObject.optJSONArray("values");
        } else {
            JSONObject currentValues = getCurrentValues(context);
            valuesJson = resolver.resolveAsArray(valuesExpression,currentValues);
        }

        String[] values = getValues(valuesJson);
        String otherOption = bundle.resolveKey(jsonObject.optString("other"));
        if (!TextUtils.isEmpty(otherOption)) {
            List<String> valuesWithOther = new ArrayList<>(Arrays.asList(values));
            valuesWithOther.add(otherOption);
            values = valuesWithOther.toArray(values);
        }

        String imagesExpression = getImagesAsJsonExpression(jsonObject, resolver);

        JSONArray imagesJson = null;
        if(imagesExpression == null){
            imagesJson = jsonObject.optJSONArray("images");
        } else {
            JSONObject currentValues = getCurrentValues(context);
            imagesJson = resolver.resolveAsArray(imagesExpression,currentValues);
        }

        String[] images = getValues(imagesJson);
        String[] imagesWithOther = new String[images.length+1];
        System.arraycopy(images,0, imagesWithOther, 0, images.length);
        imagesWithOther[images.length]=Integer.toString(R.mipmap.other_icon); // Add option for other

        indexToSelect = getSelectedIdx(values, valueToSelect);

        if (values != null) {
            List<CarouselItem> data = new ArrayList<>();
            for (int i = 0; i<values.length; i++){
                String imagePath = imagesWithOther[i];
                if(!TextUtils.isEmpty(imagesWithOther[i]) && !isInteger(imagesWithOther[i])) {
                    imagePath = moveAssetToCache(context, imagesWithOther[i], "imagenes");
                }
                data.add(new CarouselItem(values[i], imagePath));
            }
            scrollView.setOrientation(DSVOrientation.HORIZONTAL);
            scrollView.setSlideOnFling(true);
            scrollView.setAdapter(new CarouselAdapter(data));

            scrollView.addOnItemChangedListener(listener);
            scrollView.scrollToPosition(indexToSelect);

            scrollView.setItemTransitionTimeMillis(150);
            scrollView.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());

        }
        views.add(scrollView);
        return views;
    }

    @Nullable
    private JSONObject getCurrentValues(Context context) throws JSONException {
        JSONObject currentValues = null;
        if (context instanceof JsonApi) {
           String currentJsonState = ((JsonApi) context).currentJsonState();
           JSONObject currentJsonObject = new JSONObject(currentJsonState);
           currentValues =  JsonFormUtils.extractDataFromForm(currentJsonObject,false);
        }
        return currentValues;
    }

    private String[] getValues(JSONArray valuesJson) {
        String[] values = null;
        if (valuesJson != null && valuesJson.length() > 0) {
            final int valuesJsonLength = valuesJson.length();
            values = new String[valuesJsonLength];
            for (int i = 0; i < valuesJsonLength; i++) {
                values[i] = valuesJson.optString(i);
            }
        }
        return values;
    }

    private int getSelectedIdx(String[] values, String valueToSelect ) {
        int indexToSelect = -1;
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                 if (valueToSelect.equals(values[i])) {
                     indexToSelect = i;
                     break;
                }
            }
        }
        return indexToSelect;
    }

    private String getValuesAsJsonExpression(JSONObject jsonObject, JsonExpressionResolver resolver) {
            String valuesExpression = jsonObject.optString("values");
            if (resolver.isValidExpression(valuesExpression)) {
                return valuesExpression;
            }
            return null;
    }

    private String getImagesAsJsonExpression(JSONObject jsonObject, JsonExpressionResolver resolver) {
        String valuesExpression = jsonObject.optString("images");
        if (resolver.isValidExpression(valuesExpression)) {
            return valuesExpression;
        }
        return null;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setId(ViewUtil.generateViewId());
        final String hint = jsonObject.getString("hint");
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        editText.setText(jsonObject.optString("value"));
        editText.setEnabled(false);
        views.add(editText);
        return views;
    }

    public static ValidationStatus validate(MaterialSpinner spinner) {
        if (!(spinner.getTag(R.id.v_required) instanceof String) || !(spinner.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null);
        }
        Boolean isRequired = Boolean.valueOf((String) spinner.getTag(R.id.v_required));
        if (!isRequired) {
            return new ValidationStatus(true, null);
        }
        int selectedItemPosition = spinner.getSelectedItemPosition();
        if(selectedItemPosition > 0) {
            return new ValidationStatus(true, null);
        }
        return new ValidationStatus(false, (String) spinner.getTag(R.id.error));
    }

    private static String moveAssetToCache(Context context, String assetName, String assetFolderName){
        File f = new File(context.getCacheDir()+File.separator+assetName);
        if (!f.exists()) try {

            InputStream is = context.getAssets().open(assetFolderName+File.separator+assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }
        return f.getAbsolutePath();
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(Exception e) {
            return false;
        }
        // only got here if we didn't return false
    }

}
