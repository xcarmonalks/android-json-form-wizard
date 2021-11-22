package com.vijay.jsonwizard.listeners;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.MaterialTextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatePickerListener implements View.OnFocusChangeListener, View.OnClickListener{

    private MaterialDatePicker d;
    private MaterialTextInputLayout dateText;
    private static final String TAG = "DatePickerListener";
    private static FragmentManager fragmentManager;

    public DatePickerListener(MaterialTextInputLayout materialEditText, FragmentManager fragmentManager) {
        this.dateText = materialEditText;
        this.fragmentManager = fragmentManager;
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

        //check if there are calendar constraints in the view, minDate and maxDate
        final String minDateStr = (String) view.getTag(R.id.minDate);
        final String maxDateStr = (String) view.getTag(R.id.maxDate);

        //seup calendar
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        //setting up calendar constraints
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setOpenAt(calendar.getTimeInMillis());
        String pattern = (String) dateText.getTag(R.id.v_pattern);
        Date minDate = resolveDate(minDateStr, pattern);
        Date maxDate = resolveDate(maxDateStr, pattern);

        if (minDate != null && maxDate != null) {
            constraintBuilder.setStart(minDate.getTime());
            constraintBuilder.setEnd(maxDate.getTime());
        }

        //build calendar picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(constraintBuilder.build());
        builder.setTheme(R.style.widget_material_calendar);
        d = builder.build();

        d.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis((long) selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String formatted = format.format(utc.getTime());
                dateText.getEditText().setText(formatted);
                d.dismiss();
            }
        });

        d.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        FragmentActivity dialog = d.getActivity();
        d.show(this.fragmentManager, "DATE_PICKER");
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

