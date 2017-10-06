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

    private static final PaymentProcess PAYMENT_PROCESS = new PaymentProcess();

    // METHODS RETURNING ModelAndView

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

        Map<String, Float> response = new HashMap<>();
        response.put("total", order.getTotalPrice());
        response.put("subtotal", newSubtotal);

        Gson gson = new Gson();
        return gson.toJson(response);
    }

    public static ModelAndView renderCheckout(Request req, Response res) {
        OrderUtils.setOrderStatus(req);
        Order order = OrderUtils.getOrderFromSessionInfo(req);

        Log.saveActionToOrderLog(order.getOrderLogFilename(), "reviewed");

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> userDatas = new HashMap<>(); // Intentionally empty
        params.put("user", userDatas);
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
        if (order != null) {
            order.setCheckoutInfo(userData);
        }
        res.redirect("/payment");

        return null;
    }

    public static ModelAndView renderPayment(Request req, Response res) {
        setOrderStatus(req);
        Order order = OrderUtils.getOrderFromSessionInfo(req);

        Log.saveActionToOrderLog(order.getOrderLogFilename(), "checkedout");

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

        Log.saveActionToOrderLog(order.getOrderLogFilename(), "paid");
        Log.saveOrderToJson(order.getId(), order.toString());

        // SEND EMAIL
        String userEmail = order.getEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            String body = Email.renderOrderTemplate(order);
            Email.send(userEmail, "order confirmation", body);
        }

        req.session().removeAttribute("order_id");

        Map<String, Object> params = new HashMap<>();
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        return new ModelAndView(params, "success");
    }

    // ACCESSORY METHODS

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

    private static Map<String, String> collectCheckoutInfo(Request req) {
        Map<String, String> userDatas = new HashMap<>();
        userDatas.put("username", req.queryParams("username"));
        userDatas.put("email", req.queryParams("email"));
        userDatas.put("phone", req.queryParams("phone"));
        userDatas.put("billcountry", req.queryParams("bill-country"));
        userDatas.put("billcity", req.queryParams("bill-city"));
        userDatas.put("billzip", req.queryParams("bill-zip"));
        userDatas.put("billaddress", req.queryParams("bill-address"));
        userDatas.put("shipcountry", req.queryParams("ship-country"));
        userDatas.put("shipcity", req.queryParams("ship-city"));
        userDatas.put("shipzip", req.queryParams("ship-zip"));
        userDatas.put("shipaddress", req.queryParams("ship-address"));
        return userDatas;
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
