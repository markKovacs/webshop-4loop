package com.codecool.shop.processing;

import com.codecool.shop.Main;
import com.codecool.shop.order.InputField;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.util.Map;

public class PaymentProcess extends AbstractProcess {

    @Override
    public boolean action(Order order, String paymentType, Map<String, String> inputValues) {

        boolean validPaymentInfo = false;
        if (paymentType.equals("credit-card")) {
            validPaymentInfo = validateCreditCard(inputValues);
        } else if (paymentType.equals("paypal")) {
            validPaymentInfo = validatePayPalAccount(inputValues);
        }
        float toBePaid = order.getTotalPrice();
        if (validPaymentInfo && sufficientFunds(toBePaid)) {
            // TODO: insufficient funds need a separate redirection handling
            deductTotalPrice(toBePaid);
            order.setStatus(Status.PAID);
            return true;
        }
        return false;
    }

    private static boolean validateCreditCard(Map<String, String> inputValues) {
        if (InputField.CARD_NUM_PART.validate(inputValues.get("card-number-1")) &&
                InputField.CARD_NUM_PART.validate(inputValues.get("card-number-2")) &&
                InputField.CARD_NUM_PART.validate(inputValues.get("card-number-3")) &&
                InputField.CARD_NUM_PART.validate(inputValues.get("card-number-4")) &&
                InputField.CARD_HOLDER.validate(inputValues.get("card-holder")) &&
                InputField.CVC.validate(inputValues.get("cvc")) &&
                InputField.EXP_YEAR.validate(inputValues.get("exp-year")) &&
                InputField.EXP_MONTH.validate(inputValues.get("exp-month"))) {
            return true;
        }
        return false;
    }

    private static boolean validatePayPalAccount(Map<String, String> inputValues) {
        if (InputField.PAYPAL_USERNAME.validate(inputValues.get("username")) &&
                InputField.PAYPAL_PASSWORD.validate(inputValues.get("password"))) {
            return true;
        }
        return false;
    }

    private static boolean sufficientFunds(float toBePaid) {
        return (Main.balanceInUSD - toBePaid) > 0.0f;
    }

    private static void deductTotalPrice(float toBePaid) {
        Main.balanceInUSD -= toBePaid;
    }

}
