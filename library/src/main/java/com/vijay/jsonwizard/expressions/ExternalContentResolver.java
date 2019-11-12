package com.vijay.jsonwizard.expressions;

import android.content.Context;

import org.json.JSONObject;

public interface ExternalContentResolver {
    public void setContext(Context context);

    public JSONObject loadExtenalContent(String id);
}
