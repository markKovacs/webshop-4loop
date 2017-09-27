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

import java.util.HashMap;
import java.util.Map;

public class ProductController {

    public static ModelAndView renderProducts(Request req, Response res) {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("category", productCategoryDataStore.find(1));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));

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

}
