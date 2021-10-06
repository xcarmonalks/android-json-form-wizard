package com.vijay.jsonwizard.validators.textinputlayout;

public class MinLengthValidator extends LengthValidator{

    public MinLengthValidator(String errorMessage, int minLength) {
        super(errorMessage, minLength, Integer.MAX_VALUE);
    }
}
