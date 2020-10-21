package com.vijay.jsonwizard.demo.state;

import android.net.Uri;

public class StateContract {

    public static final String FORM_STATE = "com.jsonwizard.state";

    public static final String ITEM_TYPE = "vnd.android.cursor.item/checklist_state";

    public final static String COL_JSON = "JSON";

    private StateContract() {
    }

    public static Uri buildUri() {
        String authority = "com.vijay.jsonwizard.demo";
        return Uri.parse("content://" + authority + "/" + FORM_STATE);
    }

}
