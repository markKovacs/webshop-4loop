package com.codecool.shop.controller;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.OrderDaoMem;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import com.codecool.shop.order.Order;
import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {

    public static ModelAndView renderProducts(Request req, Response res) {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("category", productCategoryDataStore.find(2));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(2)));

        Order order = OrderDaoMem.getInstance().find(getSessionOrderId(req));
        int cartItems = order != null ? OrderDaoMem.getInstance().find(getSessionOrderId(req)).countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

    public static String addToCart(Request req, Response res) {
        int quantity = Integer.valueOf(req.queryParams("quantity"));
        int productId = Integer.valueOf(req.queryParams("product_id"));
        int orderId = getSessionOrderId(req);

        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(orderId);
        }
        if (order == null) {
            order = new Order();
            OrderDaoMem.getInstance().add(order);
        }

        String statusMessage = order.addToCart(productId, quantity);
        req.session().attribute("order_id", order.getId());
        System.out.println(order);
        return statusMessage;
    }

    private static int getSessionOrderId(Request req) {
        return req.session().attribute("order_id") == null ? -1 :
                Integer.valueOf(req.session().attribute("order_id")+"");
    }

    public static ModelAndView reviewCart(Request req, Response res) {

        Map params = new HashMap<>();
        List<Order> orderItems = new ArrayList<>();
        params.put("order", orderItems);

        if (getSessionOrderId(req) != -1) {
            System.out.println(OrderDaoMem.getInstance().find(getSessionOrderId(req)));
            params.put("order", OrderDaoMem.getInstance().find(getSessionOrderId(req)).getItems());
        }

        return new ModelAndView(params, "review");
    }

}
