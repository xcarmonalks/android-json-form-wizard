package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

/**
 * Created by jurkiri on 22/11/17.
 */

public class EditGroupFactory implements FormWidgetFactory {

    private static final String TAG = "EditGroupFactory";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject parentJson, CommonListener listener, boolean editable) throws Exception {
        List<View> viewsFromJson = new ArrayList<>();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setTag(R.id.key, parentJson.getString("key"));
        linearLayout.setTag(R.id.type, JsonFormConstants.EDIT_GROUP);



        String groupTitle = parentJson.optString("title");
        if (groupTitle != null) {
            viewsFromJson.add(getTextViewWith(context, 16, groupTitle, parentJson.getString("key"),
                    parentJson.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0),
                    FONT_BOLD_PATH));
        }

        try {
            JSONArray options = parentJson.getJSONArray("options");
            long optNumber = parentJson.getLong("optNumber");
            linearLayout.setWeightSum(optNumber);
            for (int i = 0; i < optNumber; i++) {
                JSONObject childJson = options.getJSONObject(i);
                try {
                    List<View> views = WidgetFactoryRegistry.getWidgetFactory(childJson.getString("type")).getViewsFromJson(stepName, context, childJson, listener, editable);
                    for (View v : views) {
                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        layoutParams.setMargins(0,0,10,0);
                        v.setLayoutParams(layoutParams);
                        linearLayout.addView(v);
                    }
                } catch (Exception e) {
                    Log.e(TAG,
                            "Exception occurred in making child view at index : " + i + " : Exception is : "
                                    + e.getMessage());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json exception occurred : " + e.getMessage());
        }
        if (linearLayout.getChildCount() > 0) {
            viewsFromJson.add(linearLayout);
        }
        return viewsFromJson;
    }
}
