package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.utils.DateUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jurkiri on 16/11/17.
 */

public class DatePickerFactory implements FormWidgetFactory {

    private static final String TAG = "DatePickerFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle,JsonExpressionResolver resolver, int visualizationMode) throws JSONException {
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
        editText.setTag(R.id.v_pattern, bundle.resolveKey(jsonObject.getString("pattern")));

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            Date date = DateUtils.parseJSONDate(value);
            editText.setText(SimpleDateFormat.getInstance().format(date));
        }

        DatePickerListener datePickerListener = new DatePickerListener(editText);
        editText.setOnFocusChangeListener(datePickerListener);
        editText.setOnClickListener(datePickerListener);
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
            Date date = DateUtils.parseJSONDate(value);
            SimpleDateFormat widgetDateFormat = new SimpleDateFormat(widgetPattern);
            editText.setText(widgetDateFormat.format(date));
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

    private class DatePickerListener implements View.OnFocusChangeListener, View.OnClickListener {

        private Dialog d;
        private MaterialEditText dateText;

        public DatePickerListener(MaterialEditText editText){
            this.dateText = editText;
        }

        @Override
        public void onFocusChange(View view, boolean focus) {
            if(focus){
                openDatePicker(view);
            }
        }

        @Override
        public void onClick(View view) {
            openDatePicker(view);
        }

        private void openDatePicker(View view) {
            Date date = new Date();
            String dateStr = dateText.getText().toString();
            if(dateStr != null && !"".equals(dateStr)){
                try {
                    date = SimpleDateFormat.getDateInstance().parse(dateStr);
                } catch (ParseException e){
                    Log.e(TAG, "Error parsing " + dateStr + ": " + e.getMessage());
                }
            }

            Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker).date(date.getTime());
            d = builder.build(view.getContext());

            d.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = new Date(((DatePickerDialog)d).getDate());
                    dateText.setText(DateUtils.formatDate(date, (String) dateText.getTag(R.id.v_pattern)));
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
