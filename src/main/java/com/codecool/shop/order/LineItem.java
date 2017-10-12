package com.codecool.shop.order;

import com.codecool.shop.model.Product;

public class LineItem {
    //private static int idCounter = 0;
    //private int id;

    //private Product product;
    private int productId;
    private String productName;
    private String productImage;

    private int quantity;
    private float actualPrice;
    private float subTotalPrice;

    public LineItem(int productId, String productName, String productImage, int quantity, float actualPrice) {
        //this.id = idCounter;
        //idCounter++;
        //this.id = lineItemId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.actualPrice = actualPrice;
        this.subTotalPrice = quantity * actualPrice;
    }

    public void changeQuantity(int amount) {
        this.quantity += amount;
        this.subTotalPrice += amount * actualPrice;
    }

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

    public void setSubTotalPrice(int subTotalPrice) {
        this.subTotalPrice = subTotalPrice;
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
