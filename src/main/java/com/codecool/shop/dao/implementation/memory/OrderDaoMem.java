package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDaoMem implements OrderDao {

    private List<Order> DATA = new ArrayList<>();
    private static OrderDaoMem instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    private OrderDaoMem() {
    }

    public static OrderDaoMem getInstance() {
        if (instance == null) {
            instance = new OrderDaoMem();
        }
        return instance;
    }

    @Override
    public void add(Order order) {
        order.setId(DATA.size() + 1);
        DATA.add(order);
    }

    @Override
    public Order find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }


    @Override
    public void remove(int id) {
        DATA.remove(find(id));

    }

    @Override
    public List<Order> getAll() {
        return DATA;
    }

    @Override
    public List<Order> getBy(Status status) {
        return DATA.stream().filter(o -> o.getStatus().equals(status)).collect(Collectors.toList());
    }

}
