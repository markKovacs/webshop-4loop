package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;


public class LineItem {
    private static int idCounter = 0;
    private int id;
    private Product product;
    private int quantity;
    private float actualPrice;
    private float subTotalPrice;

    public LineItem(Product product, int quantity, float actualPrice) {
        this.id = idCounter;
        idCounter++;
        this.product = product;
        this.quantity = quantity;
        this.actualPrice = actualPrice;
        this.subTotalPrice = quantity * actualPrice;
    }

    public void changeQuantity(int amount) {
        this.quantity += amount;
        this.subTotalPrice += amount * actualPrice;
    }

    public void changeQuantityToValue(int quantity) {
        System.out.println(this.quantity);
        System.out.println("TO");
        System.out.println(quantity);
        this.quantity = quantity;
        this.subTotalPrice = quantity * actualPrice;
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

    public int getId() {
        return this.id;
    }

}
