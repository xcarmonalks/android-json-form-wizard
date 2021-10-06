
/**
 * Created by landa95 04/10/2021
 */

package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.vijay.jsonwizard.validators.textinputlayout.TILValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MaterialTextInputLayout extends TextInputLayout  {

    private List<TILValidator> validators;

    public MaterialTextInputLayout(@NonNull Context context) {
        super(context);
    }


    public MaterialTextInputLayout(@NonNull Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

    }

    public MaterialTextInputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs,  defStyleAttr);
    }


    public MaterialTextInputLayout addValidator(TILValidator validator){
        if (this.validators == null) {
            this.validators = new ArrayList();
        }
        this.validators.add(validator);
        return this;
    }


    public boolean validate() {
        if(validators != null && !validators.isEmpty()) {
            CharSequence text = this.getEditText().getText();
            boolean isEmpty  = text.length() == 0;
            boolean isValid = true;
            Iterator iterator = this.validators.iterator();

            while (iterator.hasNext()){
                TILValidator  validator =  (TILValidator) iterator.next();
                isValid = isValid && validator.isValid(text, isEmpty);
                if(!isValid) {
                    this.setError(validator.getErrorMessage());
                    break;
                }
            }
            if(isValid){
                this.setError(null);
            }
            this.postInvalidate();
            return isValid;
        }else{
            return true;
        }
    }

    public boolean hasValidators() {
        return this.validators != null && !this.validators.isEmpty();
    }


    public void initTextWatchers(){
        this.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(validators != null && !validators.isEmpty()) {
                    CharSequence text = MaterialTextInputLayout.this.getEditText().getText();
                    boolean isEmpty = text.length() == 0;
                    boolean isValid = true;
                    Iterator iterator = MaterialTextInputLayout.this.validators.iterator();

                    while (iterator.hasNext()) {
                        TILValidator validator = (TILValidator) iterator.next();
                        isValid = isValid && validator.isValid(text, isEmpty);
                        if (!isValid) {
                            MaterialTextInputLayout.this.setError(validator.getErrorMessage());
                            break;
                        }else{
                            MaterialTextInputLayout.this.setError(null);
                        }
                    }
                }
            }
        });
    }

}
