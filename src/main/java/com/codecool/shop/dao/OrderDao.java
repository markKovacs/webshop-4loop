package com.codecool.shop.dao;

import com.codecool.shop.order.Order;
import com.codecool.shop.order.LineItem;
import java.util.List;

public interface OrderDao {

    void increaseLineItemQuantity(Order order, LineItem lineItem, int quantity);
    Order createNewOrder(int userId);
    Order findByID(int id);
    Order findOpenByUserId(int userId);
    LineItem findLineItemInCart(int productId, Order order);
    void addLineItemToCart(LineItem lineItem, Order order);
    void changeQuantity(Order order, LineItem lineItem, int quantity);
    void removeLineItemFromCart(int productId, Order order);
    void removeZeroQuantityItems(Order order);
    List<Order> getAll();
    List<Order> getAllPaid(int userId);
    void setStatus(Order order);
    void saveCheckoutInfo(Order order);
    void closeOrder(Order order);

}
