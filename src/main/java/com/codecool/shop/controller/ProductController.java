package com.codecool.shop.controller;

import com.codecool.shop.Main;
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
import com.codecool.shop.processing.PaymentProcess;
import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {

    public static final PaymentProcess PAYMENT_PROCESS = new PaymentProcess();

    public static ModelAndView renderProducts(Request req, Response res) {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", productCategoryDataStore.find(1));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));
        params.put("balance", String.format("%.2f", Main.balanceInUSD));


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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "paypal");
    }

    public static boolean payWithChosenMethod(Request req, Response res) {
        // TODO: check if any inputs are empty (null), client side limitation is not enough

        Map<String, String> inputValues = collectPaymentInput(req);

        int orderId = getSessionOrderId(req);
        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(orderId);
        }

        String paymentType = getPaymentType(req);
        return PAYMENT_PROCESS.process(order, paymentType, inputValues);

    }

    public static ModelAndView renderSuccess(Request req, Response res) {
        req.session().removeAttribute("order_id");
        Map params = new HashMap<>();
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
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
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        int orderId = getSessionOrderId(req);
        Order order = null;
        if (orderId != -1) {
            order = OrderDaoMem.getInstance().find(getSessionOrderId(req));
        }

        if (order != null) {
            params.put("order", order.getItems());
            params.put("grandTotal", order.getTotalPrice());
        }

        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "review");
    }

    private static Map<String, String> collectPaymentInput(Request req) {
        Map<String, String> inputValues = new HashMap<>();
        inputValues.put("card-number-1", req.queryParams("card-number-1"));
        inputValues.put("card-number-2", req.queryParams("card-number-2"));
        inputValues.put("card-number-3", req.queryParams("card-number-3"));
        inputValues.put("card-number-4", req.queryParams("card-number-4"));
        inputValues.put("card-holder", req.queryParams("card-holder"));
        inputValues.put("exp-year", req.queryParams("exp-year"));
        inputValues.put("exp-month", req.queryParams("exp-month"));
        inputValues.put("cvc", req.queryParams("cvc"));
        inputValues.put("username", req.queryParams("username"));
        inputValues.put("password", req.queryParams("password"));
        return inputValues;
    }

    private static String getPaymentType(Request req) {
        String paymentType = null;
        switch (req.pathInfo()) {
            case "/payment/bank": paymentType = "credit-card"; break;
            case "/payment/paypal": paymentType = "paypal"; break;
        }
        return paymentType;
    }

}
