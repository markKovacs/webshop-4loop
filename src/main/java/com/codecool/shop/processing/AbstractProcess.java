package com.codecool.shop.processing;

import com.codecool.shop.order.Order;

import java.util.List;
import java.util.Map;

public abstract class AbstractProcess {

    public List<String> process(Order order, String paymentType, Map<String, String> inputValues) {
        return action(order, paymentType, inputValues);
    }

    public abstract List<String> action(Order order, String paymentType, Map<String, String> inputValues);

}
