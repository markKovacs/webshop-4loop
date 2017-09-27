package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;

public class LineItem {
    private Product product;
    private int quantity;
    private int actualPrice;

    public LineItem(Product product, int quantity) {
        this. product = product;
        this.quantity = quantity;
        this.actualPrice = -1;
    }

    public void changeQuantity(int amount) {
        this.quantity += amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    @Override
    public String toString() {
        return "LineItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", actualPrice=" + actualPrice +
                '}';
    }
}
