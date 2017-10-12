package com.codecool.shop.dao;

import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {

    Order createNewOrder(int userId);
    // void add(Order order);
    Order findByID(int id);
    Order findOpenByUserId(int userId);
    void remove(int id);

    List<Order> getAll();
    List<Order> getBy(Status status);

    List<Order> getAllPaid(int userId);

}
