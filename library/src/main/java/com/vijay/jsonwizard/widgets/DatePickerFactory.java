package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialTextInputLayout;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.DateUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jurkiri on 16/11/17.
 */

public class DatePickerFactory implements FormWidgetFactory {

    private static final String TAG = "DatePickerFactory";


    public static ValidationStatus validate(MaterialTextInputLayout materialTextInputLayout) {
        boolean validate = materialTextInputLayout.validate();
        if (!validate) {
            return new ValidationStatus(false, materialTextInputLayout.getError().toString());
        }
        return new ValidationStatus(true, null);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener,
                                       JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver,
                                       int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode) {
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY:
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(context, jsonObject, bundle, stepName);

        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle, String stepName)
            throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialTextInputLayout materialTextInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(
                R.layout.item_material_edit_text, null);
        final EditText editText = materialTextInputLayout.getEditText();
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        final String minDate = jsonObject.optString("minDate");
        final String maxDate = jsonObject.optString("maxDate");

        materialTextInputLayout.setHint(hint);
        materialTextInputLayout.setId(View.generateViewId());
        materialTextInputLayout.setTag(R.id.key, jsonObject.getString("key"));
        materialTextInputLayout.setTag(R.id.type, jsonObject.getString("type"));
        materialTextInputLayout.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));
        materialTextInputLayout.setTag(R.id.minDate, minDate);
        materialTextInputLayout.setTag(R.id.maxDate, maxDate);

        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        editText.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));
        editText.setTag(R.id.minDate, minDate);
        editText.setTag(R.id.maxDate, maxDate);

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            Date date = DateUtils.parseJSONDate(value);
            editText.setText(SimpleDateFormat.getInstance().format(date));
        }

        //DatepickerListener is attached in JsonFormFragment, check onViewCreated method
        views.add(materialTextInputLayout);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle)
            throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialTextInputLayout textInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(
                R.layout.item_date_picker, null);
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout.setHint(hint);
        editText.setId(View.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        textInputLayout.setTag(R.id.key, jsonObject.getString("key"));
        textInputLayout.setTag(R.id.type, jsonObject.getString("type"));
        String widgetPattern = bundle.resolveKey(jsonObject.getString("pattern"));

        editText.setTag(R.id.v_pattern, widgetPattern);
        textInputLayout.setTag(R.id.v_pattern, widgetPattern);
        
        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            Date date = DateUtils.parseJSONDate(value);
            SimpleDateFormat widgetDateFormat = new SimpleDateFormat(widgetPattern);
            editText.setText(widgetDateFormat.format(date));
        }

        editText.setEnabled(false);
        views.add(textInputLayout);
        return views;
    }
}
