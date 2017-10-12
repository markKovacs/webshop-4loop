package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.utility.Log;

import java.util.ArrayList;
import java.util.Currency;
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

        return DATA.stream().filter(order -> order.getUserId() == userId && order.getStatus() != Status.PAID)
                            .findFirst()
                            .orElse(null);
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
    public void addLineItemToOrder(Order order, Product product, int quantity) {
        LineItem lineItemToAdd = new LineItem(
                order.getId(),
                product.getId(),
                product.getName(),
                product.getImageFileName(),
                quantity,
                product.getDefaultPrice(),
                product.getDefaultCurrency()
        );
        order.getItems().add(lineItemToAdd);
    }

    @Override
    public void updateLineItemInOrder(Order order, Product product, int quantity) {
        for (LineItem lineItem : order.getItems()) {
            if (lineItem.getProductId() == product.getId()) {
                lineItem.setQuantity(quantity);
            }
        }
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
    }

    public void changeQuantity(LineItem lineItem, int quantity) {
        lineItem.setQuantity(lineItem.getQuantity() + quantity);
        lineItem.setSubTotalPrice(lineItem.getSubTotalPrice() + quantity * lineItem.getActualPrice());
    }

    @Override
    public void setStatus(Order order) {

    }

    @Override
    public void removeLineItemFromCart(int productId, Order order) {
        for (LineItem lineItem : order.getItems()) {
            if (lineItem.getProductId() == productId) {
                order.getItems().remove(lineItem);
            }
        }
    }

    @Override
    public void removeZeroQuantityItems(Order order) {
        order.getItems().removeIf(item -> item.getQuantity() == 0);
    }
}
