package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.utility.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDaoMem implements OrderDao {

    private List<Order> DATA = new ArrayList<>();
/*
    private static OrderDaoMem instance = null;
*/

    /* A private Constructor prevents any other class from instantiating.
     */
    public OrderDaoMem() {
    }

    @Override
    public Order createNewOrder(int userId) {
        int id = DATA.size() + 1;
        String orderLogFilename = Log.getNowAsString() + "_" + id + "_order";
        Order order = new Order(id, userId, orderLogFilename);
        DATA.add(order);
        return order;
    }

    /*@Override
    public void add(Order order) {
        //order.setId(DATA.size() + 1);
        DATA.add(order);
    }*/

    @Override
    public Order findByID(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Order findOpenByUserId(int userId) {
        return null;
    }

    @Override
    public void remove(int id) {
        DATA.remove(findByID(id));

    }

    @Override
    public List<Order> getAll() {
        return DATA;
    }

    @Override
    public List<Order> getBy(Status status) {
        return DATA.stream().filter(o -> o.getStatus().equals(status)).collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllPaid(int userId) {
        return null;
    }


    @Override
    public void setStatus(Order order) {

    }
}
