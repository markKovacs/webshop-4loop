package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.util.List;

public class OrderDaoJdbc implements OrderDao {

    @Override
    public void add(Order order) {

    }

    @Override
    public Order find(int id) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public List<Order> getAll() {
        return null;
    }

    @Override
    public List<Order> getBy(Status status) {
        return null;
    }

    @Override
    public List<Order> getAllPaid(int userId) {
        return null;
    }
}
