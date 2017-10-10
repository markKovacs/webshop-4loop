package com.codecool.shop.processing;

import com.codecool.shop.Main;
import com.codecool.shop.order.InputField;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentProcess extends AbstractProcess {

    @Override
    public List<String> action(Order order, String paymentType, Map<String, String> paymentData) {

        List<String> errorMessages = null;
        if (paymentType.equals("credit-card")) {
            errorMessages = validateCreditCard(paymentData);
        } else if (paymentType.equals("paypal")) {
            errorMessages = validatePayPalAccount(paymentData);
        }
        float toBePaid = order.getTotalPrice();
        if (errorMessages.size() == 0) {
            if (!sufficientFunds(toBePaid)) {
                errorMessages.add("Insufficient funds. Please transfer more money to your account to continue.");
            } else {
                deductTotalPrice(toBePaid);
                order.setStatus(Status.PAID);
            }
        }
        return errorMessages;
    }

    private static List<String> validateCreditCard(Map<String, String> paymentData) {
        List<String> errorMessages = new ArrayList<>();
        if (!InputField.CARD_NUM_PART.validate(paymentData.get("cardnumberone"))) {
            errorMessages.add("Card number fragment #1 is wrong. 4 digits required.");
        }
        if (!InputField.CARD_NUM_PART.validate(paymentData.get("cardnumbertwo"))) {
            errorMessages.add("Card number fragment #2 is wrong. 4 digits required.");
        }
        if (!InputField.CARD_NUM_PART.validate(paymentData.get("cardnumberthree"))) {
            errorMessages.add("Card number fragment #3 is wrong. 4 digits required.");
        }
        if (!InputField.CARD_NUM_PART.validate(paymentData.get("cardnumberfour"))) {
            errorMessages.add("Card number fragment #4 is wrong. 4 digits required.");
        }
        if (!InputField.CARD_HOLDER.validate(paymentData.get("cardholder"))) {
            errorMessages.add("Card holder field needs to be 4-50 characters long.");
        }
        if (!InputField.CVC.validate(paymentData.get("cvc"))) {
            errorMessages.add("CVC field needs to contain 3 digits.");
        }
        if (!InputField.EXP_YEAR.validate(paymentData.get("expyear"))) {
            errorMessages.add("Expiration year needs to be between 2017-2099.");
        }
        if (!InputField.EXP_MONTH.validate(paymentData.get("expmonth"))) {
            errorMessages.add("Expiration month needs to be between 1-12.");
        }
        return errorMessages;
    }

    private static List<String> validatePayPalAccount(Map<String, String> paymentData) {
        List<String> errorMessages = new ArrayList<>();
        if (!InputField.USERNAME.validate(paymentData.get("username"))) {
            errorMessages.add("PayPal username must be 4-50 characters long. Use standard characters. No spaces allowed.");
        }
        if (!InputField.PASSWORD.validate(paymentData.get("password"))) {
            errorMessages.add("PayPal password must be between 4-50 characters long. Use standard characters.");
        }
        return errorMessages;
    }

    private static boolean sufficientFunds(float toBePaid) {
        return (Main.balanceInUSD - toBePaid) > 0.0f;
    }

    private static void deductTotalPrice(float toBePaid) {
        Main.balanceInUSD -= toBePaid;
    }

}
