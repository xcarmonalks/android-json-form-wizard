package com.vijay.jsonwizard.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jurkiri on 16/11/17.
 */

public class DateUtils {

    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    private static final DateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    public static String toJSONDateFormat(Date date) {
        if(date == null){
            return null;
        }
        return jsonDateFormat.format(date);
    }

    public static Date parseJSONDate(String date) {
        try {
            return jsonDateFormat.parse(date);
        } catch (ParseException e) {
            //TODO - revisar
            e.printStackTrace();
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
            //TODO - revisar
            e.printStackTrace();
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
