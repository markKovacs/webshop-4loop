package com.codecool.shop.order;

import java.util.regex.Pattern;

public enum InputField {
    FULL_NAME ("^[a-zA-Z -]{5,50}$"),
    EMAIL ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"),
    PHONE ("\\d{2,3}-\\d{2,3}-\\d{6,10}"),
    COUNTRY ("^[a-zA-Z -]{2,50}$"),
    CITY ("^[a-zA-Z -]{5,50}$"),
    ZIP_CODE ("^[a-zA-Z0-9 -]{2,10}$"),
    ADDRESS ("^[a-zA-Z0-9,. -]{5,100}$"),
    CARD_NUM_PART ("\\d{4}"),
    CVC ("\\d{3}"),
    CARD_HOLDER ("^[a-zA-Z -]{5,50}$"),
    EXP_YEAR ("\\d{4}"),
    EXP_MONTH ("\\d{1,2}"),
    PAYPAL_USERNAME ("^[a-zA-Z-_]{5,50}$"),
    PAYPAL_PASSWORD ("^[a-zA-Z -_!?]{4,50}$");

    private String regex;

    InputField(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public boolean validate(String inputValue) {
        Pattern compiledPattern = Pattern.compile(this.getRegex());
        if (!compiledPattern.matcher(inputValue).matches()) {
            return false;
        }
        return true;
    }
}
