package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vijay.jsonwizard.R;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by vijay on 24-05-2015.
 */
public class FormUtils {
    public static final String FONT_BOLD_PATH = "font/Roboto-Bold.ttf";
    public static final String FONT_REGULAR_PATH = "font/Roboto-Regular.ttf";
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    public static LinearLayout.LayoutParams getLayoutParams(int width, int height, int left, int top, int right,
        int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static TextView getTextViewWith(Context context, int textSizeInSp, String text, String key, String type,
        LinearLayout.LayoutParams layoutParams, String fontPath) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTag(R.id.key, key);
        textView.setTag(R.id.type, type);
        textView.setId(View.generateViewId());
        textView.setTextSize(textSizeInSp);
        textView.setLayoutParams(layoutParams);
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        return textView;
    }

    public static TextView getTextViewWith(Context context, int textSizeInSp, Spanned text, String key, String type,
        LinearLayout.LayoutParams layoutParams, String fontPath) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTag(R.id.key, key);
        textView.setTag(R.id.type, type);
        textView.setId(View.generateViewId());
        textView.setTextSize(textSizeInSp);
        textView.setLayoutParams(layoutParams);
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        return textView;
    }

    public static int dpToPixels(Context context, float dps) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5d);
    }

    public static Map<String, Map<String, String>> parseChecklistBundle(@NonNull String json) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        Type typeOfBundle = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        return gson.fromJson(json, typeOfBundle);
    }
}
