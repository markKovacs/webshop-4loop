package com.codecool.shop.processing;

import com.codecool.shop.order.Order;

import java.util.Map;

public abstract class AbstractProcess {

    public boolean process(Order order, String paymentType, Map<String, String> inputValues) {
        return action(order, paymentType, inputValues);
    }

    public abstract boolean action(Order order, String paymentType, Map<String, String> inputValues);

}
