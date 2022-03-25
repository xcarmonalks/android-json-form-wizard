package com.vijay.jsonwizard.widgets;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.demo.resources.ResourceResolver;
import com.vijay.jsonwizard.expressions.JsonExpressionResolver;
import com.vijay.jsonwizard.i18n.JsonFormBundle;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jurkiri on 22/11/17.
 */

public class EditGroupFactory implements FormWidgetFactory {

    private static final String TAG = "EditGroupFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject parentJson, CommonListener listener,
        JsonFormBundle bundle, JsonExpressionResolver resolver, ResourceResolver resourceResolver,
        int visualizationMode) throws JSONException {


        List<View> viewsFromJson = new ArrayList<>();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setTag(R.id.key, parentJson.getString("key"));
        linearLayout.setTag(R.id.type, JsonFormConstants.EDIT_GROUP);


        String groupTitle = bundle.resolveKey(parentJson.optString("title"));
        if (!TextUtils.isEmpty(groupTitle)) {
            viewsFromJson.add(
                getTextViewWith(context, 16, groupTitle, parentJson.getString("key"), parentJson.getString("type"),
                    getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH));
        }
        long optNumber = parentJson.getLong("optNumber");
        long childrenPerLine = prepareChildrenPerLine(parentJson,optNumber);
        int childrenIndex = 0;
        while(childrenIndex <  optNumber){
            try {
                if(childrenIndex>0){
                    linearLayout = new LinearLayout(context);
                    linearLayout.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setTag(R.id.key, parentJson.getString("key"));
                    linearLayout.setTag(R.id.type, JsonFormConstants.EDIT_GROUP);
                }

                JSONArray fields = parentJson.getJSONArray(JsonFormConstants.FIELDS_FIELD_NAME);

                linearLayout.setWeightSum(childrenPerLine);
                int lineIndex = 0;
                while ( lineIndex < childrenPerLine && childrenIndex <  optNumber) {
                    JSONObject childJson = fields.getJSONObject(childrenIndex);
                    try {
                        List<View> views = WidgetFactoryRegistry.getWidgetFactory(childJson.getString("type"))
                                                                .getViewsFromJson(stepName, context, childJson, listener,
                                                                    bundle, resolver, resourceResolver, visualizationMode);
                        for (View v: views) {
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                            layoutParams.setMargins(0, 0, 10, 0);
                            v.setLayoutParams(layoutParams);
                            linearLayout.addView(v);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception occurred in making child view at index : " + childrenIndex + " : Exception is : " + e
                            .getMessage());
                    }
                    lineIndex++;
                    childrenIndex++;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Json exception occurred : " + e.getMessage());
            }
            if (linearLayout.getChildCount() > 0) {
                viewsFromJson.add(linearLayout);
            }
        }

        return viewsFromJson;
    }

    private long prepareChildrenPerLine(JSONObject parentJson, long optNumber) {
        if(parentJson.has("childrenPerLine")){
            try {
                return parentJson.getLong("childrenPerLine");
            } catch (JSONException e) {
                e.printStackTrace();
                return optNumber;
            }
        }else{
            return optNumber;
        }
    }
}
