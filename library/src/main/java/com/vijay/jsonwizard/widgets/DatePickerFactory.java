package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.vijay.jsonwizard.R;
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

import java.text.DateFormat;
import java.text.ParseException;
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
                views = getEditableViewsFromJson(context, jsonObject, bundle);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle)
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

        DatePickerListener datePickerListener = new DatePickerListener(materialTextInputLayout);
        editText.setOnFocusChangeListener(datePickerListener);
        editText.setOnClickListener(datePickerListener);
        editText.setInputType(InputType.TYPE_NULL);

        views.add(materialTextInputLayout);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle)
            throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialTextInputLayout textInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(
                R.layout.item_material_edit_text, null);
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

    private class DatePickerListener implements View.OnFocusChangeListener, View.OnClickListener {

        private Dialog d;
        private MaterialTextInputLayout dateText;

        public DatePickerListener(MaterialTextInputLayout materialEditText) {
            this.dateText = materialEditText;
        }

        @Override
        public void onFocusChange(View view, boolean focus) {
            if (focus) {
                openDatePicker(view);
            }
        }

        @Override
        public void onClick(View view) {
            openDatePicker(view);
        }

        private void openDatePicker(View view) {
            Date date = new Date();
            String dateStr = dateText.getEditText().getText().toString();
            if (dateStr != null && !"".equals(dateStr)) {
                try {
                    date = SimpleDateFormat.getDateInstance().parse(dateStr);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing " + dateStr + ": " + e.getMessage());
                }
            }

            final String minDateStr = (String) view.getTag(R.id.minDate);
            final String maxDateStr = (String) view.getTag(R.id.maxDate);
            String pattern = (String) dateText.getTag(R.id.v_pattern);
            Date minDate = resolveDate(minDateStr, pattern);
            Date maxDate = resolveDate(maxDateStr, pattern);

            DatePickerDialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker)
                    .date(date.getTime());
            if (minDate != null && maxDate != null) {
                builder.dateRange(minDate.getTime(), maxDate.getTime());
            }
            d = builder.build(view.getContext());

            d.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = new Date(((DatePickerDialog) d).getDate());
                    dateText.getEditText().setText(DateUtils.formatDate(date, (String) dateText.getTag(R.id.v_pattern)));
                    d.dismiss();
                }
            });

            d.negativeActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });

            d.positiveAction("OK").negativeAction("CANCEL");
            d.show();
        }

        private Date resolveDate(String dateStr, String pattern) {
            DateFormat sdf;
            if (TextUtils.isEmpty(pattern)) {
                sdf = SimpleDateFormat.getDateInstance();
            } else {
                sdf = new SimpleDateFormat(pattern);
            }
            try {
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing " + dateStr + ": " + e.getMessage());
            }
            return null;
        }
    }
}
