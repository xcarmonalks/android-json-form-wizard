package com.vijay.jsonwizard.interfaces;

import com.rey.material.widget.Switch;
import com.vijay.jsonwizard.utils.CarouselAdapter;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;

/**
 * Created by vijay on 5/17/15.
 */
public interface CommonListener extends View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        Spinner.OnItemSelectedListener,
        Switch.OnCheckedChangeListener,
        OnFieldStateChangeListener,
        DiscreteScrollView.ScrollStateChangeListener<CarouselAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<CarouselAdapter.ViewHolder> {
}
