package com.codecool.shop.processing;

import com.codecool.shop.order.Order;

import java.util.Map;

public class CheckoutProcess extends AbstractProcess {

    @Override
    public boolean action(Order order, String paymentType, Map<String, String> inputValues) {
        return false;
    }

}
