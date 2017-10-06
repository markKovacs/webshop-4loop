package com.codecool.shop.controller;

import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
import com.codecool.shop.dao.implementation.OrderDaoMem;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.processing.PaymentProcess;
import com.codecool.shop.utility.Email;
import com.codecool.shop.utility.Log;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codecool.shop.OrderUtils.setOrderStatus;

public class OrderController {
    public static final PaymentProcess PAYMENT_PROCESS = new PaymentProcess();

    public static ModelAndView renderPayment(Request req, Response res) {
        setOrderStatus(req);
        Order order = OrderUtils.getOrderFromSessionInfo(req);

        // LOGGING
        try {
            Log.save("admin", order.getOrderLogFilename(), Log.getNowAsString() + ": Order has been payed!");
        } catch (IOException e) {
            System.out.println("Error saving admin log!");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("order", order);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "payment");
    }

    public static String addToCart(Request req, Response res) {
        int quantity = Integer.parseInt(req.queryParams("quantity"));
        int productId = Integer.parseInt(req.queryParams("product_id"));

        Order order = OrderUtils.getOrderFromSessionInfo(req);

        if (order == null) {
            order = new Order();
            OrderDaoMem.getInstance().add(order);
        }

        String statusMessage = order.addToCart(productId, quantity);
        req.session().attribute("order_id", order.getId());

        return statusMessage;
    }

    public static String changeProductQuantity(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int quantity = Integer.parseInt(req.queryParams("quantity"));
        int productId = Integer.parseInt(req.queryParams("product_id"));

        float newSubtotal = order.changeProductQuantity(productId, quantity);
        order.updateTotal();

        Map<String, Float> response = new HashMap<>();
        response.put("total", order.getTotalPrice());
        response.put("subtotal", newSubtotal);

        Gson gson = new Gson();
        return gson.toJson(response);
    }

    public static ModelAndView renderBankPayment(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "bank");
    }

    public static ModelAndView renderPayPalPayment(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "paypal");
    }

    public static ModelAndView payWithChosenMethod(Request req, Response res) {
        // TODO: check if any inputs are empty (null), client side limitation is not enough

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        Map<String, String> inputValues = collectPaymentInput(req);
        String paymentType = getPaymentType(req);

        boolean successfulPayment = PAYMENT_PROCESS.process(order, paymentType, inputValues);
        if (successfulPayment) {
            order = OrderUtils.getOrderFromSessionInfo(req);
            if (order != null) {
                System.out.println("Status set to PAID");
                order.setStatus(Status.PAID);
            }
            res.redirect("/payment/success");
        } else {
            res.redirect(req.pathInfo());
        }

        return null;
    }

    public static ModelAndView renderSuccess(Request req, Response res) {

        Order order = OrderUtils.getOrderFromSessionInfo(req);

        // LOGGING
        try {
            String timestamp = Log.getNowAsString();
            Log.save("order", timestamp + "_" + order.getId() + "_order", order.toString());
            Log.save("admin", order.getOrderLogFilename(), timestamp + ": Order has been closed successfully!");
        } catch (IOException e) {
            System.out.println("Error saving payed order!");
        }

        // SEND EMAIL
        String userEmail = order.getEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            String body = Email.renderOrderTemplate(order);
            Email.send(userEmail, "order confirmation", body);
        }

        req.session().removeAttribute("order_id");

        Map params = new HashMap<>();
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "success");
    }

    public static ModelAndView renderReview(Request req, Response res) {

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        List<LineItem> orderItems = new ArrayList<>();
        float totalPrice = 0f;
        int cartItems = 0;

        if (order != null) {
            orderItems = order.getItems();
            totalPrice = order.getTotalPrice();
            cartItems = order.countCartItems();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("order", order);
        params.put("items", orderItems);
        params.put("grandTotal", String.format("%.2f", totalPrice));
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
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
