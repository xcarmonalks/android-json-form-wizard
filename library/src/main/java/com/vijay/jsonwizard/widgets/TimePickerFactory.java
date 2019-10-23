package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.app.Dialog;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.DateUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimePickerFactory implements FormWidgetFactory {

    private static final String TAG = "TimePickerFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver, int visualizationMode) throws JSONException {
        List<View> views = null;
        switch (visualizationMode){
            case JsonFormConstants.VISUALIZATION_MODE_READ_ONLY :
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle);
                break;
            default:
                views = getEditableViewsFromJson(context, jsonObject, bundle);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));

        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        String widgetPattern = bundle.resolveKey(jsonObject.getString("pattern"));
        editText.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));


        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            try {
                Date date = DateUtils.parseJSONDate(value);
                SimpleDateFormat dateFormatter = new SimpleDateFormat(widgetPattern);
                editText.setText(dateFormatter.format(date));
            }catch (IllegalArgumentException e){
                Log.e(TAG, "Error parsing " + value + ": " + e.getMessage());
            }
        }

        TimePickerListener timePickerListener = new TimePickerListener(editText);
        editText.setOnFocusChangeListener(timePickerListener);
        editText.setOnClickListener(timePickerListener);
        editText.setInputType(InputType.TYPE_NULL);

        views.add(editText);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle) throws JSONException {
        List<View> views = new ArrayList<>(1);
        final MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));

        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        String widgetPattern = bundle.resolveKey(jsonObject.getString("pattern"));
        editText.setTag(R.id.v_pattern, widgetPattern);

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            try {
                Date date = DateUtils.parseJSONDate(value);
                SimpleDateFormat widgetDateFormat = new SimpleDateFormat(widgetPattern);
                editText.setText(widgetDateFormat.format(date));
            }catch (IllegalArgumentException e){
                Log.e(TAG, "Error parsing " + value + ": " + e.getMessage());
            }
        }

        editText.setEnabled(false);
        views.add(editText);
        return views;
    }

    public static ValidationStatus validate(MaterialEditText editText) {
        boolean validate = editText.validate();
        if(!validate) {
            return new ValidationStatus(false, editText.getError().toString());
        }
        return new ValidationStatus(true, null);
    }

    private class TimePickerListener implements View.OnFocusChangeListener, View.OnClickListener {

        private Dialog d;
        private MaterialEditText timeText;

        public TimePickerListener(MaterialEditText editText){
            this.timeText = editText;
        }

        @Override
        public void onFocusChange(View view, boolean focus) {
            if(focus){
                openTimePicker(view);
            }
        }

        @Override
        public void onClick(View view) {
            openTimePicker(view);
        }

        private void openTimePicker(View view) {
            int hour=0;
            int minute=0;
            String timeStr = timeText.getText().toString();
            String pattern = (String) timeText.getTag(R.id.v_pattern);
            if(timeStr != null && !"".equals(timeStr)){
                try {
                    SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateFormatter.parse(timeStr));
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);

                } catch (ParseException e){
                    Log.e(TAG, "Error parsing " + timeStr + ": " + e.getMessage());
                }
            }else{
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            TimePickerDialog.Builder builder = new TimePickerDialog.Builder(hour,minute);

            d = builder.build(view.getContext());

            d.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectedHour=((TimePickerDialog)d).getHour();
                    int selectedMinute=((TimePickerDialog)d).getMinute();
                    timeText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    d.hide();
                }
            });

            d.negativeActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.hide();
                }
            });

            d.positiveAction("OK").negativeAction("CANCEL");
            d.show();
        }

    }

}

