package com.codecool.shop.controller;

import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.order.InputField;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.processing.PaymentProcess;
import com.codecool.shop.user.User;
import com.codecool.shop.utility.Email;
import com.codecool.shop.utility.Log;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codecool.shop.OrderUtils.setOrderStatus;

public class OrderController {

    // METHODS RETURNING RAW RESPONSE

    public static String handleAddToCart(Request req, Response res) {

        int quantity = Integer.parseInt(req.queryParams("quantity"));
        int productId = Integer.parseInt(req.queryParams("product_id"));

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        if (order == null) {
            order = DaoFactory.getOrderDao().createNewOrder(userId);
        }

        String statusMessage = order.addToCart(productId, quantity);

        return statusMessage;
    }

    public static ModelAndView renderReview(Request req, Response res) {
        Order order = OrderUtils.setOrderStatus(req);

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
        params.put("loggedIn", req.session().attribute("user_id") != null);

        return new ModelAndView(params, "review");
    }

    public static String changeQuantity(Request req, Response res) {

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        int quantity = Integer.parseInt(req.queryParams("quantity"));
        int productId = Integer.parseInt(req.queryParams("product_id"));

        float newSubtotal = order.changeProductQuantity(productId, quantity);
        order.updateTotal();

        Map<String, Float> newPrices = new HashMap<>();
        newPrices.put("total", order.getTotalPrice());
        newPrices.put("subtotal", newSubtotal);

        Gson gson = new Gson();
        return gson.toJson(newPrices);
    }

    public static String removeLineItem(Request req, Response res) {
        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        int productId = Integer.parseInt(req.queryParams("product_id"));

        //order.removeLineItem(productId);
        DaoFactory.getOrderDao().removeLineItemFromCart(productId, order);
        order.updateTotal();

        boolean cartIsEmpty = order.getItems().isEmpty();
/*        if (cartIsEmpty) {
            DaoFactory.getOrderDao().removeOrder(order.getId());
        }*/

        Map<String, Object> newPrices = new HashMap<>();
        newPrices.put("total", order.getTotalPrice());
        newPrices.put("cartIsEmpty", cartIsEmpty);

        Gson gson = new Gson();
        return gson.toJson(newPrices);
    }

    public static ModelAndView finalizeOrder(Request req, Response res) {

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        order.getItems().forEach(System.out::println);
        DaoFactory.getOrderDao().removeZeroQuantityItems(order);

        if (order.getItems().size() < 1) {
            //DaoFactory.getOrderDao().removeOrder(order.getId());
            res.redirect("/cart");
        } else {
            Log.saveActionToOrderLog(order.getOrderLogFilename(), "reviewed");
            res.redirect("/checkout?next=process");
        }

        return null;
    }

    public static ModelAndView renderCheckout(Request req, Response res) {
        Order order = OrderUtils.setOrderStatus(req);
        int userId = req.session().attribute("user_id");

        int cartItems = order != null ? order.countCartItems() : 0;

        User user = DaoFactory.getUserDao().find(userId);

        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("loggedIn", req.session().attribute("user_id") != null);
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "checkout");
    }

    public static ModelAndView doCheckout(Request req, Response res) {
        Map<String, String> userData = collectCheckoutInfo(req);
        List<String> errorMessages = checkForInvalidInput(userData);

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);
        int cartItems = order != null ? order.countCartItems() : 0;

        if (errorMessages.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("user", userData);
            params.put("errors", errorMessages);
            params.put("balance", String.format("%.2f", Main.balanceInUSD));
            params.put("loggedIn", req.session().attribute("user_id") != null);
            params.put("cartItems", cartItems);
            return new ModelAndView(params, "checkout");
        }

        order.setCheckoutInfo(userData);
        DaoFactory.getOrderDao().saveCheckoutInfo(order);

        Log.saveActionToOrderLog(order.getOrderLogFilename(), "checkedout");
        res.redirect("/payment?next=process");
        return null;
    }

    public static ModelAndView renderPayment(Request req, Response res) {
        Order order = setOrderStatus(req);
        Map<String, Object> params = new HashMap<>();
        params.put("order", order);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("loggedIn", req.session().attribute("user_id") != null);
        return new ModelAndView(params, "payment");
    }

    public static ModelAndView renderBankPayment(Request req, Response res) {
        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("payment", new HashMap<String, String>());
        params.put("loggedIn", req.session().attribute("user_id") != null);

        return new ModelAndView(params, "bank");
    }

    public static ModelAndView renderPayPalPayment(Request req, Response res) {
        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("cartItems", cartItems);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("payment", new HashMap<String, String>());
        params.put("loggedIn", req.session().attribute("user_id") != null);

        return new ModelAndView(params, "paypal");
    }

    public static ModelAndView payWithChosenMethod(Request req, Response res) {
        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        Map<String, String> paymentData = collectPaymentInfo(req);
        String paymentType = getPaymentType(req);

        PaymentProcess paymentProcess = new PaymentProcess();
        List<String> errorMessages = paymentProcess.process(order, paymentType, paymentData);

        if (errorMessages.size() == 0) {
            order.setStatus(Status.PAID);
            DaoFactory.getOrderDao().setStatus(order);

            Log.saveActionToOrderLog(order.getOrderLogFilename(), "paid");
            Log.saveOrderToJson(order);

            String to = order.getEmail();
            String body = Email.renderEmailTemplate("order_email",
                    new HashMap<String, Object>(){{ put("order", order); }});
            String subject = "Order Confirmation";
            // Email.send(to, body, subject);

            res.redirect("/payment/success");
        } else {
            int cartItems = order != null ? order.countCartItems() : 0;
            Map<String, Object> params = new HashMap<>();
            params.put("cartItems", cartItems);
            params.put("balance", String.format("%.2f", Main.balanceInUSD));
            params.put("payment", paymentData);
            params.put("errors", errorMessages);
            params.put("loggedIn", req.session().attribute("user_id") != null);
            String view = req.pathInfo().contains("bank") ? "bank" : "paypal";
            return new ModelAndView(params, view);
        }

        return null;
    }

    public static ModelAndView renderSuccess(Request req, Response res) {

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);
        int cartItems = order != null ? order.countCartItems() : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("balance", String.format("%.2f", Main.balanceInUSD));
        params.put("loggedIn", req.session().attribute("user_id") != null);
        params.put("cartItems", cartItems);

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
        userData.put("fullName", req.queryParams("username"));
        userData.put("email", req.queryParams("email"));
        userData.put("phone", req.queryParams("phone"));
        userData.put("billingCountry", req.queryParams("bill-country"));
        userData.put("billingCity", req.queryParams("bill-city"));
        userData.put("billingZipCode", req.queryParams("bill-zip"));
        userData.put("billingAddress", req.queryParams("bill-address"));
        userData.put("shippingCountry", req.queryParams("ship-country"));
        userData.put("shippingCity", req.queryParams("ship-city"));
        userData.put("shippingZipCode", req.queryParams("ship-zip"));
        userData.put("shippingAddress", req.queryParams("ship-address"));
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
        if (!InputField.FULL_NAME.validate(userData.get("fullName"))) {
            errorMessages.add("Invalid username.");
        }
        if (!InputField.EMAIL.validate(userData.get("email"))) {
            errorMessages.add("Invalid email address");
        }
        if (!InputField.PHONE.validate(userData.get("phone"))) {
            errorMessages.add("Invalid phone number");
        }
        if (!InputField.COUNTRY.validate(userData.get("billingCountry"))) {
            errorMessages.add("Invalid billing country");
        }
        if (!InputField.CITY.validate(userData.get("billingCity"))) {
            errorMessages.add("Invalid billing city");
        }
        if (!InputField.ZIP_CODE.validate(userData.get("billingZipCode"))) {
            errorMessages.add("Invalid billing ZIP code");
        }
        if (!InputField.ADDRESS.validate(userData.get("billingAddress"))) {
            errorMessages.add("Invalid billing address");
        }
        if (!InputField.COUNTRY.validate(userData.get("shippingCountry"))) {
            errorMessages.add("Invalid shipping country");
        }
        if (!InputField.CITY.validate(userData.get("shippingCity"))) {
            errorMessages.add("Invalid shipping city");
        }
        if (!InputField.ZIP_CODE.validate(userData.get("shippingZipCode"))) {
            errorMessages.add("Invalid shipping ZIP code");
        }
        if (!InputField.ADDRESS.validate(userData.get("shippingAddress"))) {
            errorMessages.add("Invalid shipping address");
        }
        return errorMessages;
    }

}
