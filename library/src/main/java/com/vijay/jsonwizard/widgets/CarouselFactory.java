package com.vijay.jsonwizard.widgets;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialTextInputLayout;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.CarouselAdapter;
import com.vijay.jsonwizard.utils.CarouselItem;
import com.vijay.jsonwizard.utils.ExpressionResolverContextUtils;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xcarmona on 21/06/18.
 */
public class CarouselFactory implements FormWidgetFactory {

    private static final String TAG = "CarouselFactory";

    public static ValidationStatus validate(DiscreteScrollView dsv) {
        if (!(dsv.getTag(R.id.v_required) instanceof String) || !(dsv.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null);
        }
        Boolean isRequired = Boolean.valueOf((String) dsv.getTag(R.id.v_required));
        if (!isRequired) {
            return new ValidationStatus(true, null);
        }
        int selectedItemPosition = dsv.getCurrentItem();
        if (selectedItemPosition > 0) {
            return new ValidationStatus(true, null);
        }
        return new ValidationStatus(false, (String) dsv.getTag(R.id.error));
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
        JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver,
        int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode) {
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY:
                views = getReadOnlyViewsFromJson(context, jsonObject);
                break;
            default:
                views = getEditableViewsFromJson(stepName, context, jsonObject, listener, bundle, resolver, resourceResolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
        JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver)
        throws JSONException {
        List<View> views = new ArrayList<>(1);

        DiscreteScrollView scrollView = (DiscreteScrollView) LayoutInflater.from(context).inflate(
            R.layout.item_carousel, null);

        scrollView.setId(ViewUtil.generateViewId());

        scrollView.setTag(R.id.key, jsonObject.getString("key"));
        scrollView.setTag(R.id.type, jsonObject.getString("type"));

        //Skip required part. One is always selected
        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                scrollView.setTag(R.id.v_required, requiredValue);
                scrollView.setTag(R.id.error, bundle.resolveKey(requiredObject.optString("err")));
            }
        }

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
            JSONObject currentValues = getCurrentValues(context, stepName);
            valuesJson = resolver.resolveAsArray(valuesExpression, currentValues);
        }

        String imagesExpression = getImagesAsJsonExpression(jsonObject, resolver);

        JSONArray imagesJson;
        if (imagesExpression == null) {
            imagesJson = jsonObject.optJSONArray("images");
        } else {
            JSONObject currentValues = getCurrentValues(context, stepName);
            imagesJson = resolver.resolveAsArray(imagesExpression, currentValues);
        }

        String[] values = getValues(valuesJson);
        String[] images = getValues(imagesJson);

        List<String> listValues = new ArrayList<>(Arrays.asList(values));
        List<String> listNames = new ArrayList<>(Arrays.asList(values));
        List<String> listImages = new ArrayList<>(Arrays.asList(images));


        String otherOption = bundle.resolveKey(jsonObject.optString("other"));
        String chooseOption = bundle.resolveKey(jsonObject.optString("hint"));
        chooseOption = (TextUtils.isEmpty(chooseOption)) ? context.getString(R.string.image_picker) : chooseOption;

        //Add choose option
        listValues.add(0, null);
        listNames.add(0, chooseOption);
        listImages.add(0, Integer.toString(R.mipmap.choose_icon));

        if (!TextUtils.isEmpty(otherOption)) {
            //Add other option
            listValues.add(otherOption);
            listNames.add(otherOption);
            listImages.add(Integer.toString(R.mipmap.other_icon));
        }

        values = listValues.toArray(values);

        indexToSelect = getSelectedIdx(values, valueToSelect);

        if (values != null && values.length > 1) {
            List<CarouselItem> data = new ArrayList<>();
            for (int i = 0; i < listValues.size(); i++) {
                String imagePath = listImages.get(i);
                if (!isInteger(imagePath)) {
                    imagePath = resourceResolver.resolvePath(context, imagePath);
                }
                data.add(new CarouselItem(listNames.get(i), listValues.get(i), imagePath));
            }
            scrollView.setOrientation(DSVOrientation.HORIZONTAL);
            scrollView.setSlideOnFling(true);
            scrollView.setAdapter(new CarouselAdapter(data));

            scrollView.addOnItemChangedListener(listener);
            scrollView.scrollToPosition(indexToSelect);

            scrollView.setItemTransitionTimeMillis(120);
            scrollView.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());

        }
        views.add(scrollView);
        return views;
    }

    @Nullable
    private JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        return ExpressionResolverContextUtils.getCurrentValues(context, stepName);
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

    private int getSelectedIdx(String[] values, String valueToSelect) {
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

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject) throws JSONException {
        List<View> views = new ArrayList<>(1);
        MaterialTextInputLayout textInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(R.layout.item_material_edit_text,
                null);
        EditText editText = textInputLayout.getEditText();
        editText.setId(ViewUtil.generateViewId());
        textInputLayout.setTag(R.id.key, jsonObject.getString("key"));
        textInputLayout.setTag(R.id.type, jsonObject.getString("type"));
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        editText.setText(jsonObject.optString("value"));
        editText.setEnabled(false);
        views.add(textInputLayout);
        return views;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
        // only got here if we didn't return false
    }
}
