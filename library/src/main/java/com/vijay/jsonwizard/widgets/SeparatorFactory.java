package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jurkiri on 23/11/17.
 */

public class SeparatorFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener, JsonFormBundle bundle, int visualizationMode) throws JSONException {
        List<View> views = new ArrayList<>(1);
        View v = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,10);
        layoutParams.height=2;
        v.setBackgroundColor(context.getResources().getColor(R.color.primary));

        v.setLayoutParams(layoutParams);
        views.add(v);
        return views;
    }
}
