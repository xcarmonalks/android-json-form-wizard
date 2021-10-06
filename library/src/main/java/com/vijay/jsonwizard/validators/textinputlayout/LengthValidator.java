package com.vijay.jsonwizard.validators.textinputlayout;

public class LengthValidator extends TILValidator {

    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;

    public LengthValidator(String errorMessage, int minLength, int maxLength) {
        super(errorMessage);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(CharSequence charSequence, boolean isEmpty) {
        if (!isEmpty) {
            if (charSequence.length() >= minLength && charSequence.length() <= maxLength) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
