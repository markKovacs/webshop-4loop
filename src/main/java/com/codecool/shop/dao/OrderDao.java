package com.codecool.shop.dao;

import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.util.List;

public interface OrderDao {

    void add(Order order);
    Order find(int id);
    void remove(int id);

    List<Order> getAll();
    List<Order> getBy(Status status);

}
