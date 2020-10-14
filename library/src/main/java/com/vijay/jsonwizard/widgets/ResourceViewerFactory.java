package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.JsonFormUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourceViewerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context, JSONObject jsonObject, final CommonListener listener,
                                       JsonFormBundle bundle, JsonExpressionResolver resolver,
                                       ResourceResolver resourceResolver, int visualizationMode) throws Exception {
        List<View> views = new ArrayList<>(1);
        View parentView = LayoutInflater.from(context).inflate(R.layout.item_resource_label, null);

        TextView label = parentView.findViewById(R.id.label);
        label.setTag(R.id.type, JsonFormConstants.RESOURCE_VIEWER);

        String resource = jsonObject.getString("resource");
        String resourcePath = resourceResolver.resolvePath(context, resource);
        if (resourcePath != null && new File(resourcePath).exists()) {
            label.setTag(R.id.value, resourcePath);
        } else {
            label.setTag(R.id.value, resource);
        }
        String labelText = bundle.resolveKey(jsonObject.getString("label"));
        label.setTag(R.id.label, labelText);
        label.setText(labelText);
        label.setOnClickListener(listener);
        if (jsonObject.has("config")) {
            String expression = jsonObject.optString("config");
            JSONObject config;
            if (resolver.isValidExpression(expression)) {
                JSONObject currentValues = getCurrentValues(context);
                config = resolver.resolveAsObject(expression, currentValues);
            } else {
                config = jsonObject.getJSONObject("config");
            }
            configureLabel(label, config);
        }

        views.add(parentView);
        return views;
    }

    private void configureLabel(TextView label, JSONObject config) {
        String color = config.optString("color");
        if (color != null) {
            label.setTextColor(Color.parseColor(color));
        } else {
            label.setTextColor(Color.BLUE);
        }
        Double size = config.optDouble("size");
        if (size != null) {
            label.setTextSize(size.floatValue());
        }
        String alignment = config.optString("align");
        if (alignment != null) {
            switch (alignment) {
                case "end":
                    label.setGravity(Gravity.END);
                    break;
                case "center":
                    label.setGravity(Gravity.CENTER);
                    break;
                default:
                    label.setGravity(Gravity.START);
                    break;
            }
        }
    }

    @Nullable
    private JSONObject getCurrentValues(Context context) throws JSONException {
        JSONObject currentValues = null;
        if (context instanceof JsonApi) {
            String currentJsonState = ((JsonApi) context).currentJsonState();
            JSONObject currentJsonObject = new JSONObject(currentJsonState);
            currentValues = JsonFormUtils.extractDataFromForm(currentJsonObject, false);
        }
        return currentValues;
    }

}
