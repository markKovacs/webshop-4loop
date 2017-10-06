package com.codecool.shop.processing;

import com.codecool.shop.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutProcess extends AbstractProcess {

    @Override
    public List<String> action(Order order, String paymentType, Map<String, String> inputValues) {
        return new ArrayList<>();
    }

}
