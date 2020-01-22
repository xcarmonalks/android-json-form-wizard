package com.vijay.jsonwizard.demo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.utils.PropertiesUtils;

public class FormPausedReceiver extends BroadcastReceiver {

    public static final String FORM_PAUSED_ACTION = "jsonFormPaused";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FORM_PAUSED_ACTION.equals(intent.getAction())) {
            String json = intent.getStringExtra("json");
            PropertiesUtils.getInstance(context).setFormId("testFormId");
            PropertiesUtils.getInstance(context).setFormJson(json);
            PropertiesUtils.getInstance(context).setPausedStep(intent.getStringExtra("pausedStep"));
        }
    }
}
