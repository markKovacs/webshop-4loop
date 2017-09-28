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
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", productCategoryDataStore.find(1));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));

        Order order = OrderDaoMem.getInstance().find(getSessionOrderId(req));
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

    public static ModelAndView renderProductsBySupplier(Request req, Response res){
        int supplierId = Integer.parseInt(req.queryParams("supplier-id"));
        ProductDao productDataStore = ProductDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", supplierDataStore.find(supplierId));
        params.put("products", productDataStore.getBy(supplierDataStore.find(supplierId)));

        Order order = OrderDaoMem.getInstance().find(getSessionOrderId(req));
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

    public static ModelAndView renderProductsByCategory(Request req, Response res){
        int categoryId = Integer.parseInt(req.queryParams("category-id"));
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", productCategoryDataStore.find(categoryId));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(categoryId)));

        Order order = OrderDaoMem.getInstance().find(getSessionOrderId(req));
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

    public static ModelAndView renderPayment(Request req, Response res) {
        int orderId = getSessionOrderId(req);
        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(orderId);
        }

        Map params = new HashMap<>();
        params.put("order", order);

        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "payment");
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

    public static ModelAndView renderBankPayment(Request req, Response res) {
        int orderId = getSessionOrderId(req);
        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(orderId);
        }

        Map params = new HashMap<>();
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "bank");
    }

    public static ModelAndView renderPayPalPayment(Request req, Response res) {
        int orderId = getSessionOrderId(req);
        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(orderId);
        }

        Map params = new HashMap<>();
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "paypal");
    }

    public static String payWithCreditCard(Request req, Response res) {
/*        String cardNumber = req.queryParams("card-number1") + '-' + req.queryParams("card-number2") + '-' +
                req.queryParams("card-number3") + '-' + req.queryParams("card-number4");
        int cvc = Integer.valueOf(req.queryParams("cvc"));
        int expiryYear = Integer.valueOf(req.queryParams("exp-year"));
        int expiryMonth = Integer.valueOf(req.queryParams("exp-month"));
        String cardHolder = req.queryParams("card-holder");*/

        // TODO: validate input fields in payment process, send info if this is credit card payment or paypal

        String statusMessage = "success";
        return statusMessage;
    }

    public static String payWithPayPal(Request req, Response res) {
/*        String userName = req.queryParams("username");
        String password = req.queryParams("password");*/

        // TODO: validate input fields in payment process, send info if this is credit card payment or paypal

        String statusMessage = "success";
        return statusMessage;
    }

    public static ModelAndView renderSuccess(Request req, Response res) {
        Map params = new HashMap<>();
        return new ModelAndView(params, "success");
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
