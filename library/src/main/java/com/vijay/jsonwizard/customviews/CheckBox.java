package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.rey.material.drawable.CheckBoxDrawable;

public class CheckBox extends CompoundButton {

    public CheckBox(Context context) {
        super(context);

        internalInit(context, null, 0, 0);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        internalInit(context, attrs, 0, 0);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        internalInit(context, attrs, defStyleAttr, 0);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        internalInit(context, attrs, defStyleAttr, defStyleRes);
    }

    private void internalInit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        internalApplyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    public void applyStyle(int resId) {
        internalApplyStyle(getContext(), null, 0, resId);
    }

    private void internalApplyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        CheckBoxDrawable drawable = new CheckBoxDrawable.Builder(context, attrs, defStyleAttr, defStyleRes).build();
        drawable.setInEditMode(isInEditMode());
        drawable.setAnimEnable(false);
        setButtonDrawable(null);
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        drawable.setAnimEnable(true);
    }

    /**
     * Change the checked state of this button immediately without showing
     * animation.
     * 
     * @param checked
     *            The checked state.
     */
    public void setCheckedImmediately(boolean checked) {
        if (mButtonDrawable instanceof CheckBoxDrawable) {
            CheckBoxDrawable drawable = (CheckBoxDrawable) mButtonDrawable;
            drawable.setAnimEnable(false);
            setChecked(checked);
            drawable.setAnimEnable(true);
        } else {
            setChecked(checked);
        }
    }

}