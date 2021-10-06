/**
 * Created by landa95
 */

package com.vijay.jsonwizard.validators.textinputlayout;

public class RequiredValidator extends TILValidator{

    public RequiredValidator(String errorMessage){
        super(errorMessage);
    }

    @Override
    public boolean isValid(CharSequence charSequence, boolean isEmpty) {

        if(charSequence.toString().trim().isEmpty()){
            return false;
        }

        return true;
    }
}
