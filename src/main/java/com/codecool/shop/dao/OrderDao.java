package com.codecool.shop.dao;

import com.codecool.shop.model.Product;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.order.LineItem;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao {

    Order createNewOrder(int userId);
    // void add(Order order);
    Order findByID(int id);
    Order findOpenByUserId(int userId);
    LineItem findLineItemInCart(int productId, Order order);
    void addLineItemToCart(LineItem lineItem, Order order);
    void changeQuantity(LineItem lineItem, int quantity);


    //void addLineItemToOrder(int productId, Order order, int quantity, float actualPrice, String currency);
    //void updateLineItemInOrder(int productId, Order order, int quantity);

    void addLineItemToOrder(Order order, Product product, int quantity);
    void updateLineItemInOrder(Order order, Product product, int quantity);

    void remove(int id);
    void removeLineItemFromCart(int productId, Order order);
    void removeZeroQuantityItems(Order order);

    List<Order> getAll();
    List<Order> getBy(Status status);

    List<Order> getAllPaid(int userId);

    void setStatus(Order order);

}
