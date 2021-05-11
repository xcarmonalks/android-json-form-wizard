package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.JsonFormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.edittext.MaxLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BarcodeTextFactory implements FormWidgetFactory {

    public static ValidationStatus validate(MaterialEditText editText) {
        boolean validate = editText.validate();
        if (!validate) {
            return new ValidationStatus(false, editText.getError().toString());
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
                views = getReadOnlyViewsFromJson(context, jsonObject, bundle, resolver);
                break;
            default:
                views = getEditableViewsFromJson(stepName, context, jsonObject, listener, bundle, resolver);
        }
        return views;
    }

    private List<View> getEditableViewsFromJson(String stepName, Context context, JSONObject jsonObject,
        CommonListener listener, JsonFormBundle bundle, JsonExpressionResolver resolver) throws JSONException {

        String readonlyValue = jsonObject.optString("readonly");
        boolean readonly = false;

        if (resolver.isValidExpression(readonlyValue)) {
            JSONObject currentValues = getCurrentValues(context);
            readonly = resolver.existsExpression(readonlyValue, currentValues);
        } else {
            readonly = Boolean.TRUE.toString().equalsIgnoreCase(readonlyValue);
        }

        if (readonly) {
            return getReadOnlyViewsFromJson(context, jsonObject, bundle, resolver);
        }

        int minLength;
        int maxLength;
        List<View> views = new ArrayList<>(1);
        View parentView = LayoutInflater.from(context).inflate(R.layout.item_barcode_edit_text, null);
        final MaterialEditText editText = parentView.findViewById(R.id.edit_text);
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));

        final ImageView imageView = parentView.findViewById(R.id.icon);
        imageView.setOnClickListener(listener);
        imageView.setTag(R.id.key, jsonObject.getString("key"));
        imageView.setTag(R.id.type, jsonObject.getString("type"));

        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            String resolvedValue;
            if (resolver.isValidExpression(value)) {
                resolvedValue = resolver.resolveAsString(value, getCurrentValues(context));
                if (resolvedValue == null) {
                    resolvedValue = "";
                }
            } else {
                resolvedValue = value;
            }
            editText.setText(resolvedValue);
        }

        if (!TextUtils.isEmpty(jsonObject.optString("lines"))) {
            editText.setSingleLine(false);
            editText.setLines(jsonObject.optInt("lines"));
        }

        //add validators
        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                boolean required = false;
                if (resolver.isValidExpression(requiredValue)) {
                    JSONObject currentValues = getCurrentValues(context);
                    required = resolver.existsExpression(requiredValue, currentValues);
                } else {
                    required = Boolean.TRUE.toString().equalsIgnoreCase(requiredValue);
                }

                if (required) {
                    editText.addValidator(new RequiredValidator(bundle.resolveKey(requiredObject.getString("err"))));
                }
            }
        }

        JSONObject minLengthObject = jsonObject.optJSONObject("v_min_length");
        if (minLengthObject != null) {
            String minLengthValue = minLengthObject.optString("value");
            if (!TextUtils.isEmpty(minLengthValue)) {
                minLength = Integer.parseInt(minLengthValue);
                editText.addValidator(new MinLengthValidator(bundle.resolveKey(minLengthObject.getString("err")),
                    Integer.parseInt(minLengthValue)));
                editText.setMinCharacters(minLength);
            }
        }

        JSONObject maxLengthObject = jsonObject.optJSONObject("v_max_length");
        if (maxLengthObject != null) {
            String maxLengthValue = maxLengthObject.optString("value");
            if (!TextUtils.isEmpty(maxLengthValue)) {
                maxLength = Integer.parseInt(maxLengthValue);
                editText.addValidator(new MaxLengthValidator(bundle.resolveKey(maxLengthObject.getString("err")),
                    Integer.parseInt(maxLengthValue)));
                editText.setMaxCharacters(maxLength);
            }
        }

        JSONObject regexObject = jsonObject.optJSONObject("v_regex");
        if (regexObject != null) {
            String regexValue = regexObject.optString("value");
            if (!TextUtils.isEmpty(regexValue)) {
                editText.addValidator(new RegexpValidator(bundle.resolveKey(regexObject.getString("err")), regexValue));
            }
        }

        JSONObject emailObject = jsonObject.optJSONObject("v_email");
        if (emailObject != null) {
            String emailValue = emailObject.optString("value");
            if (!TextUtils.isEmpty(emailValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(emailValue)) {
                    editText.addValidator(new RegexpValidator(bundle.resolveKey(emailObject.getString("err")),
                        android.util.Patterns.EMAIL_ADDRESS.toString()));
                }
            }
        }

        JSONObject urlObject = jsonObject.optJSONObject("v_url");
        if (urlObject != null) {
            String urlValue = urlObject.optString("value");
            if (!TextUtils.isEmpty(urlValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(urlValue)) {
                    editText.addValidator(new RegexpValidator(bundle.resolveKey(urlObject.getString("err")),
                        Patterns.WEB_URL.toString()));
                }
            }
        }

        JSONObject numericObject = jsonObject.optJSONObject("v_numeric");
        if (numericObject != null) {
            String numericValue = numericObject.optString("value");
            if (!TextUtils.isEmpty(numericValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                    editText.addValidator(
                        new RegexpValidator(bundle.resolveKey(numericObject.getString("err")), "[0-9]+"));
                }
            }
        }

        // edit type check
        String editType = jsonObject.optString("edit_type");
        if (!TextUtils.isEmpty(editType)) {
            if (editType.equals("number")) {
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        }

        editText.addTextChangedListener(new GenericTextWatcher(stepName, editText));
        views.add(parentView);
        return views;
    }

    private List<View> getReadOnlyViewsFromJson(Context context, JSONObject jsonObject, JsonFormBundle bundle, JsonExpressionResolver resolver)
        throws JSONException {
        List<View> views = new ArrayList<>(1);
        View parentView = LayoutInflater.from(context).inflate(R.layout.item_barcode_edit_text, null);
        final MaterialEditText editText = parentView.findViewById(R.id.edit_text);
        editText.setId(ViewUtil.generateViewId());
        final String hint = bundle.resolveKey(jsonObject.getString("hint"));
        editText.setHint(hint);
        editText.setFloatingLabelText(hint);
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        final String value = jsonObject.optString("value");
        if (!TextUtils.isEmpty(value)) {
            editText.setText(value);
            String resolvedValue;
            if (resolver.isValidExpression(value)) {
                resolvedValue = resolver.resolveAsString(value, getCurrentValues(context));
                if (resolvedValue == null) {
                    resolvedValue = "";
                }
            } else {
                resolvedValue = value;
            }
            editText.setText(resolvedValue);
        }

        if (!TextUtils.isEmpty(jsonObject.optString("lines"))) {
            editText.setSingleLine(false);
            editText.setLines(jsonObject.optInt("lines"));
        }
        editText.setEnabled(false);

        final ImageView imageView = parentView.findViewById(R.id.icon);
        imageView.setClickable(false);
        imageView.setEnabled(false);
        imageView.setVisibility(View.GONE);

        views.add(parentView);
        return views;
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
