package com.vijay.jsonwizard.state;

import android.content.Context;
import android.net.Uri;

public class StateContract {

    public static final String FORM_STATE = "com.jsonwizard.state";

    public static final String ITEM_TYPE = "vnd.android.cursor.item/form_state";

    public final static String COL_JSON = "JSON";

    private StateContract() {
    }

    public static Uri buildUri(Context context) {
        String authority = String.format("%s.jsonwizard.formstate", context.getPackageName());
        return Uri.parse("content://" + authority + "/" + FORM_STATE);
    }

}
