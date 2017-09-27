package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;

public class LineItem {
    private Product product;
    private int quantity;
    private float actualPrice;
    private float subTotalPrice;

    public LineItem(Product product, int quantity, float actualPrice) {
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

    public float getSubTotalPrice() {
        return subTotalPrice;
    }

    public void setSubTotalPrice(int subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    public String getProductName() {
        return this.product.getName();
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
