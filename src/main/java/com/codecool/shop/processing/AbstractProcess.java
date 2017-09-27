package com.codecool.shop.processing;

import com.codecool.shop.order.Order;

public abstract class AbstractProcess {

    public void process(Order order) {
        action(order);
    }

    public abstract void action(Order order);

}
