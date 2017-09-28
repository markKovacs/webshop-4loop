package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;


public class LineItem {
    private static int idCounter = 0;
    private int id;
    private Product product;
    private int quantity;
    private float actualPrice;

    public LineItem(Product product, int quantity, float actualPrice) {
        this.id = idCounter;
        idCounter++;
        this.product = product;
        this.quantity = quantity;
        this.actualPrice = actualPrice;
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

    public float getActualPrice() {
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

    public String getProductName() {
        return this.product.getName();
    }

    public int getId() {
        return this.id;
    }

}
