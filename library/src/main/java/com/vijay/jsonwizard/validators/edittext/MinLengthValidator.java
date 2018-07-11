package com.vijay.jsonwizard.validators.edittext;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class MinLengthValidator extends LengthValidator {

    public MinLengthValidator(String errorMessage, int minLength) {
        super(errorMessage, minLength, Integer.MAX_VALUE);
    }
}
