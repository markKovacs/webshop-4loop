package com.codecool.shop.order;

import com.codecool.shop.model.Product;

import java.util.Currency;

public class LineItem {
    //private static int idCounter = 0;
    //private int id;

    //private Product product;
    private int orderId;
    private int productId;
    private String productName;
    private String productImage;

    private int quantity;
    private float actualPrice;
    private Currency currency;
    private float subTotalPrice;

    public LineItem(int orderId, int productId, String productName, String productImage, int quantity, float actualPrice, Currency currency) {
        //this.id = idCounter;
        //idCounter++;
        //this.id = lineItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.actualPrice = actualPrice;
        this.subTotalPrice = quantity * actualPrice;
        this.currency = currency;
    }

    /*public void changeQuantity(int amount) {
        this.quantity += amount;
        this.subTotalPrice += amount * actualPrice;
    }*/

    public float changeQuantityToValue(int quantity) {
        this.quantity = quantity;
        this.subTotalPrice = quantity * actualPrice;
        return this.subTotalPrice;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

    public void setSubTotalPrice(float subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "\t{\n" +
                "\t\"product\": \"" + productName + "\",\n"+
                "\t\"quantity\": " + quantity + ",\n" +
                "\t\"actualPrice\": " + actualPrice + "\n" +
                "\t}";
    }

}
