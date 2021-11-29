package com.vijay.jsonwizard.interfaces;

import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

public interface ClickableFormWidget{

    void onClick(JsonFormFragment jsonFormFragment, View v);

    void onFocusChange(JsonFormFragment jsonFormFragment, boolean focus, View v);

}
