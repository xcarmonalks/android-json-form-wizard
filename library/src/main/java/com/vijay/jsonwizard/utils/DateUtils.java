package com.vijay.jsonwizard.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jurkiri on 16/11/17.
 */

public class DateUtils {

    private static final String TAG = "DateUtils";

    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
    private static final String JSON_DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";

    public static String toJSONDateFormat(Date date) {
        if(date == null){
            return null;
        }
        final DateFormat jsonSdf = new SimpleDateFormat(JSON_DATE_PATTERN);
        return jsonSdf.format(date);
    }

    public static Date parseJSONDate(String date) {
        try {
            final DateFormat jsonSdf = new SimpleDateFormat(JSON_DATE_PATTERN);
            return jsonSdf.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Date parseDate(String date, String pattern) {
        DateFormat sdf;
        try{
            if(pattern == null || "".equals(pattern)){
                sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
            }else{
                sdf = new SimpleDateFormat(pattern);
            }
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static String formatDate(Date date, String pattern) {
        DateFormat sdf;
        if(pattern == null || "".equals(pattern)){
            sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        }else{
            sdf = new SimpleDateFormat(pattern);
        }
        return sdf.format(date);
    }
}
