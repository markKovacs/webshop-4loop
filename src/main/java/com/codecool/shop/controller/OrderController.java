package com.codecool.shop.controller;

import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
import com.codecool.shop.dao.implementation.OrderDaoMem;
import com.codecool.shop.order.InputField;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codecool.shop.OrderUtils.setOrderStatus;

public class OrderController {

    // METHODS RETURNING RAW RESPONSE

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

    public static ModelAndView renderReview(Request req, Response res) {
        OrderUtils.setOrderStatus(req);

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

    public static String changeQuantity(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int quantity = Integer.parseInt(req.queryParams("quantity"));
        int productId = Integer.parseInt(req.queryParams("product_id"));

        float newSubtotal = order.changeProductQuantity(productId, quantity);
        order.updateTotal();
        // TODO: if all stuff is zero, remove order too

        Map<String, Float> response = new HashMap<>();
        response.put("total", order.getTotalPrice());
        response.put("subtotal", newSubtotal);

        Gson gson = new Gson();
        return gson.toJson(response);
    }

    public static String removeLineItem(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int productId = Integer.parseInt(req.queryParams("product_id"));

        order.removeLineItem(productId);
        order.updateTotal();

        boolean cartIsEmpty = order.getItems().isEmpty();
        if (cartIsEmpty) {
            OrderDaoMem.getInstance().remove(order.getId());
            req.session().removeAttribute("order_id");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", order.getTotalPrice());
        response.put("cartIsEmpty", cartIsEmpty);

        Gson gson = new Gson();
        return gson.toJson(response);
    }

    public static ModelAndView finalizeOrder(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);

        order.getItems().removeIf(item -> item.getQuantity() == 0);

        if (order.getItems().size() < 1) {
            OrderDaoMem.getInstance().remove(order.getId());
            req.session().removeAttribute("order_id");
            res.redirect("/cart");
        } else {
            Log.saveActionToOrderLog(order.getOrderLogFilename(), "reviewed");
            res.redirect("/checkout?next=process");
        }

        return null;
    }

    public static ModelAndView renderCheckout(Request req, Response res) {
        OrderUtils.setOrderStatus(req);

        Map<String, Object> params = new HashMap<>();
        params.put("user", new HashMap<>());
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        return new ModelAndView(params, "checkout");
    }

    public static ModelAndView doCheckout(Request req, Response res) {
        Map<String, String> userData = collectCheckoutInfo(req);
        List<String> errorMessages = checkForInvalidInput(userData);

        if (errorMessages.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("user", userData);
            params.put("errors", errorMessages);
            params.put("balance", String.format("%.2f", Main.balanceInUSD));
            return new ModelAndView(params, "checkout");
        }

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        order.setCheckoutInfo(userData);
        Log.saveActionToOrderLog(order.getOrderLogFilename(), "checkedout");

        res.redirect("/payment?next=process");
        return null;
    }

    public static ModelAndView renderPayment(Request req, Response res) {
        setOrderStatus(req);
        Order order = OrderUtils.getOrderFromSessionInfo(req);

        Map<String, Object> params = new HashMap<>();
        params.put("order", order);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        return new ModelAndView(params, "payment");
    }

    public static ModelAndView renderBankPayment(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("payment", new HashMap<String, String>());

        return new ModelAndView(params, "bank");
    }

    public static ModelAndView renderPayPalPayment(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("payment", new HashMap<String, String>());

        return new ModelAndView(params, "paypal");
    }

    public static ModelAndView payWithChosenMethod(Request req, Response res) {
        Order order = OrderUtils.getOrderFromSessionInfo(req);
        Map<String, String> paymentData = collectPaymentInfo(req);
        String paymentType = getPaymentType(req);

        PaymentProcess paymentProcess = new PaymentProcess();
        List<String> errorMessages = paymentProcess.process(order, paymentType, paymentData);

        if (errorMessages.size() == 0) {
            order.setStatus(Status.PAID);
            Log.saveActionToOrderLog(order.getOrderLogFilename(), "paid");
            Log.saveOrderToJson(order);
            Email.send(order);
            res.redirect("/payment/success");
        } else {
            int cartItems = order != null ? order.countCartItems() : 0;
            Map<String, Object> params = new HashMap<>();
            params.put("cartItems", cartItems);
            params.put("balance", String.format("%.2f", Main.balanceInUSD));
            params.put("payment", paymentData);
            params.put("errors", errorMessages);
            String view = req.pathInfo().contains("bank") ? "bank" : "paypal";
            return new ModelAndView(params, view);
        }

        return null;
    }

    public static ModelAndView renderSuccess(Request req, Response res) {

        req.session().removeAttribute("order_id");

        Map<String, Object> params = new HashMap<>();
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "success");
    }

    // ACCESSORY METHODS

    private static Map<String, String> collectPaymentInfo(Request req) {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("cardnumberone", req.queryParams("card-number-1"));
        paymentData.put("cardnumbertwo", req.queryParams("card-number-2"));
        paymentData.put("cardnumberthree", req.queryParams("card-number-3"));
        paymentData.put("cardnumberfour", req.queryParams("card-number-4"));
        paymentData.put("cardholder", req.queryParams("card-holder"));
        paymentData.put("expyear", req.queryParams("exp-year"));
        paymentData.put("expmonth", req.queryParams("exp-month"));
        paymentData.put("cvc", req.queryParams("cvc"));
        paymentData.put("username", req.queryParams("username"));
        paymentData.put("password", req.queryParams("password"));
        return paymentData;
    }

    private static Map<String, String> collectCheckoutInfo(Request req) {
        Map<String, String> userData = new HashMap<>();
        userData.put("username", req.queryParams("username"));
        userData.put("email", req.queryParams("email"));
        userData.put("phone", req.queryParams("phone"));
        userData.put("billcountry", req.queryParams("bill-country"));
        userData.put("billcity", req.queryParams("bill-city"));
        userData.put("billzip", req.queryParams("bill-zip"));
        userData.put("billaddress", req.queryParams("bill-address"));
        userData.put("shipcountry", req.queryParams("ship-country"));
        userData.put("shipcity", req.queryParams("ship-city"));
        userData.put("shipzip", req.queryParams("ship-zip"));
        userData.put("shipaddress", req.queryParams("ship-address"));
        return userData;
    }

    private static String getPaymentType(Request req) {
        String paymentType = null;
        switch (req.pathInfo()) {
            case "/payment/bank": paymentType = "credit-card"; break;
            case "/payment/paypal": paymentType = "paypal"; break;
        }
        return paymentType;
    }

    private static List<String> checkForInvalidInput(Map<String, String> userData) {
        List<String> errorMessages = new ArrayList<>();
        if (!InputField.FULL_NAME.validate(userData.get("username"))) {
            errorMessages.add("Invalid username.");
        }
        if (!InputField.EMAIL.validate(userData.get("email"))) {
            errorMessages.add("Invalid email address");
        }
        if (!InputField.PHONE.validate(userData.get("phone"))) {
            errorMessages.add("Invalid phone number");
        }
        if (!InputField.COUNTRY.validate(userData.get("billcountry"))) {
            errorMessages.add("Invalid billing country");
        }
        if (!InputField.CITY.validate(userData.get("billcity"))) {
            errorMessages.add("Invalid billing city");
        }
        if (!InputField.ZIP_CODE.validate(userData.get("billzip"))) {
            errorMessages.add("Invalid billing ZIP code");
        }
        if (!InputField.ADDRESS.validate(userData.get("billaddress"))) {
            errorMessages.add("Invalid billing address");
        }
        if (!InputField.COUNTRY.validate(userData.get("shipcountry"))) {
            errorMessages.add("Invalid shipping country");
        }
        if (!InputField.CITY.validate(userData.get("shipcity"))) {
            errorMessages.add("Invalid shipping city");
        }
        if (!InputField.ZIP_CODE.validate(userData.get("shipzip"))) {
            errorMessages.add("Invalid shipping ZIP code");
        }
        if (!InputField.ADDRESS.validate(userData.get("shipaddress"))) {
            errorMessages.add("Invalid shipping address");
        }
        return errorMessages;
    }

}
