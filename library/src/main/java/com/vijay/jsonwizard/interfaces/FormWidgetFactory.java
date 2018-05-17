package com.vijay.jsonwizard.interfaces;

import android.content.Context;
import android.view.View;

import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by vijay on 24-05-2015.
 */
public interface FormWidgetFactory {

    List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
            CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver,
            int visualizationMode) throws Exception;

}
