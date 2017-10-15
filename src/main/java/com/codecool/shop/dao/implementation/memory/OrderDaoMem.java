package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.utility.Log;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDaoMem implements OrderDao {

    private static OrderDaoMem instance = null;

    private List<Order> DATA = new ArrayList<>();
    private int orderSequence = 0;

    private OrderDaoMem() {
    }

    public static OrderDaoMem getInstance() {
        if (instance == null) {
            instance = new OrderDaoMem();
        }
        return instance;
    }

    @Override
    public Order createNewOrder(int userId) {
        int id = ++orderSequence;
        String orderLogFilename = Log.getNowAsString() + "_" + id + "_order";
        Order order = new Order(id, userId, orderLogFilename);
        DATA.add(order);
        return order;
    }

    @Override
    public Order findByID(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Order findOpenByUserId(int userId) {

        return DATA.stream().filter(order -> order.getUserId() == userId && order.getStatus() != Status.PAID)
                            .findFirst()
                            .orElse(null);
    }

    @Override
    public List<Order> getAll() {
        return DATA;
    }

    @Override
    public List<Order> getAllPaid(int userId) {
        return DATA.stream().filter(order -> order.getUserId() == userId && order.getStatus().equals(Status.PAID))
                .collect(Collectors.toList());
    }

    @Override
    public LineItem findLineItemInCart(int productId, Order order) {
        return order.getItems().stream()
                               .filter(lineItem -> lineItem.getProductId() == productId)
                               .findFirst()
                               .orElse(null);
    }

    @Override
    public void addLineItemToCart(LineItem lineItem, Order order) {
        order.getItems().add(lineItem);
        order.updateTotal();
    }

    @Override
    public void increaseLineItemQuantity(Order order, LineItem lineItem, int quantity) {
        lineItem.setQuantity(lineItem.getQuantity() + quantity);
        lineItem.setSubTotalPrice(lineItem.getQuantity() * lineItem.getActualPrice());
        order.updateTotal();
    }

    public void changeQuantity(Order order, LineItem lineItem, int quantity) {
        lineItem.setQuantity(quantity);
        lineItem.setSubTotalPrice(quantity * lineItem.getActualPrice());
        order.updateTotal();
    }

    @Override
    public void setStatus(Order order) {
        // Logic done already on called side
    }

    @Override
    public void closeOrder(Order order) {
        order.setStatus(Status.PAID);
        order.setClosedDate(new Date());
    }

    @Override
    public void removeLineItemFromCart(int productId, Order order) {
        order.getItems().removeIf(lineItem -> lineItem.getProductId() == productId);
        for (LineItem lineItem : order.getItems()) {
            if (lineItem.getProductId() == productId) {
                order.getItems().remove(lineItem);
            }
        }
        order.updateTotal();
    }

    @Override
    public void removeZeroQuantityItems(Order order) {
        order.getItems().removeIf(item -> item.getQuantity() == 0);
    }

    @Override
    public void saveCheckoutInfo(Order order) {
        // Logic done already on called side
    }

}
