package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;

public class LineItem {
    private Product product;
    private int quantity;

    public LineItem(int productId, int quantity) {
        this. product = ProductDaoMem.getInstance().find(productId);
        this.quantity = quantity;
    }
}
