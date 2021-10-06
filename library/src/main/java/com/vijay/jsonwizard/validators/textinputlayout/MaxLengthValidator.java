package com.vijay.jsonwizard.validators.textinputlayout;

public class MaxLengthValidator extends LengthValidator{

    public MaxLengthValidator(String errorMessage, int maxLength) {
        super(errorMessage, 0, maxLength);
    }

}
