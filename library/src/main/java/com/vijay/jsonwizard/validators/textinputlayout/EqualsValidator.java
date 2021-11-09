package com.vijay.jsonwizard.validators.textinputlayout;

public class EqualsValidator extends TILValidator{
    private final String mCompareValue;
    private final boolean mNumeric;

    public EqualsValidator(String compareValue, String errorMessage) {
        this(compareValue, errorMessage, false);
    }

    public EqualsValidator(String compareValue, String errorMessage, boolean numeric) {
        super(errorMessage);
        mCompareValue = compareValue;
        mNumeric = numeric;
    }

    @Override
    public boolean isValid(CharSequence charSequence, boolean isEmpty) {
        if (mNumeric) {
            return isEmpty || Double.valueOf(mCompareValue).equals(Double.valueOf(charSequence.toString()));
        }
        return isEmpty || mCompareValue.equals(charSequence.toString());
    }
}
