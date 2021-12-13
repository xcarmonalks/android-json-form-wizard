package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialTextInputLayout;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.ClickableFormWidget;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.DateUtils;
import com.vijay.jsonwizard.utils.ExpressionResolverContextUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.textinputlayout.RequiredValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

public class TimePickerFactory implements FormWidgetFactory, ClickableFormWidget {

    private static final String TAG = "TimePickerFactory";

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
                views = getEditableViewsFromJson(stepName, context, jsonObject, bundle, resolver, listener);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context, JSONObject jsonObject, JsonFormBundle bundle, JsonExpressionResolver resolver, CommonListener listener)
        throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialTextInputLayout materialTextInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(
                R.layout.item_material_edit_text, null);
        final EditText editText = materialTextInputLayout.getEditText();
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));

        materialTextInputLayout.setHint(hint);
        editText.setId(View.generateViewId());
        materialTextInputLayout.setTag(R.id.key, jsonObject.getString("key"));
        materialTextInputLayout.setTag(R.id.type, JsonFormConstants.TIME_PICKER);
        materialTextInputLayout.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, JsonFormConstants.TIME_PICKER);
        String widgetPattern = bundle.resolveKey(jsonObject.getString("pattern"));
        editText.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));
        materialTextInputLayout.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            try {
                Date date = DateUtils.parseDate(value, widgetPattern);
                SimpleDateFormat dateFormatter = new SimpleDateFormat(widgetPattern);
                editText.setText(dateFormatter.format(date));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error parsing " + value + ": " + e.getMessage());
            }
        }

        //add validators
        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                boolean required = false;
                if (resolver.isValidExpression(requiredValue)) {
                    JSONObject currentValues = getCurrentValues(context, stepName);
                    required = resolver.existsExpression(requiredValue, currentValues);
                } else {
                    required = Boolean.TRUE.toString().equalsIgnoreCase(requiredValue);
                }

                if (required) {
                    materialTextInputLayout.addValidator(new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                }
            }
        }

        editText.setInputType(InputType.TYPE_NULL);
        editText.setOnClickListener(listener);
        editText.setOnFocusChangeListener(listener);

        views.add(materialTextInputLayout);
        materialTextInputLayout.initTextWatchers();
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle)
        throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialTextInputLayout textInputLayout = (MaterialTextInputLayout) LayoutInflater.from(context).inflate(
            R.layout.item_edit_text, null);
        final EditText editText = textInputLayout.getEditText();
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));

        textInputLayout.setHint(hint);
        textInputLayout.setId(View.generateViewId());
        textInputLayout.setTag(R.id.key, jsonObject.getString("key"));
        textInputLayout.setTag(R.id.type, jsonObject.getString("type"));
        editText.setId(View.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        String widgetPattern = bundle.resolveKey(jsonObject.getString("pattern"));

        editText.setTag(R.id.v_pattern, widgetPattern);
        textInputLayout.setTag(R.id.v_pattern, widgetPattern);

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            try {
                editText.setText(value);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error parsing " + value + ": " + e.getMessage());
            }
        }

        editText.setEnabled(false);
        views.add(editText);
        return views;
    }

    @Nullable
    private JSONObject getCurrentValues(Context context, String stepName) throws JSONException {
        return ExpressionResolverContextUtils.getCurrentValues(context, stepName);
    }

    @Override
    public void  onClick(JsonFormFragment jsonFormFragment, View v) {
        jsonFormFragment.hideKeyBoard();
        TimePickerListener timePickerListener = new TimePickerListener((TextInputEditText) v, (String) v.getTag(R.id.v_pattern), jsonFormFragment.getActivity().getSupportFragmentManager());
        timePickerListener.openTimePicker(v);
    }

    @Override
    public void onFocusChange(JsonFormFragment jsonFormFragment, boolean focus, View v) {
        if (focus) {
            jsonFormFragment.hideKeyBoard();
            TimePickerListener timePickerListener = new TimePickerListener((TextInputEditText) v, (String) v.getTag(R.id.v_pattern), jsonFormFragment.getActivity().getSupportFragmentManager());
            timePickerListener.openTimePicker(v);
        }
    }

    private class TimePickerListener {

        private MaterialTimePicker d;
        private TextInputEditText timeText;
        private static final String TAG = "TimePickerListener";
        private String formatString;
        private FragmentManager fragmentManager;

        private TimePickerListener(TextInputEditText textInputEditText, String formatString, FragmentManager fragmentManager) {
            this.timeText = textInputEditText;
            this.formatString = formatString;
            this.fragmentManager = fragmentManager;
        }
        
        private void openTimePicker(View view) {
            int hour = 0;
            int minute = 0;
            String timeStr = timeText.getText().toString();
            String pattern = (String) timeText.getTag(R.id.v_pattern);
            if (timeStr != null && !"".equals(timeStr)) {
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateFormatter.parse(timeStr));
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);

                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing " + timeStr + ": " + e.getMessage());
                }
            } else {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(hour).setMinute(minute);
            d = builder.build();

            d.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeText.setText(String.format("%02d:%02d", d.getHour(), d.getMinute()));
                    d.dismiss();
                }
            });

            d.show(this.fragmentManager, TAG);
        }
    }
}

