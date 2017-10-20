package com.vijay.jsonwizard.interfaces;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.rey.material.widget.Switch;

/**
 * Created by vijay on 5/17/15.
 */
public interface CommonListener extends View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        Spinner.OnItemSelectedListener,
        Switch.OnCheckedChangeListener, OnValueChangeListener {

}
