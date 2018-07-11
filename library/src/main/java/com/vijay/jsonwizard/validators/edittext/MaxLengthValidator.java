package com.vijay.jsonwizard.validators.edittext;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class MaxLengthValidator extends LengthValidator {

    public MaxLengthValidator(String errorMessage, int maxLength) {
        super(errorMessage, 0, maxLength);
    }
}
