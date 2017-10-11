package com.codecool.shop.controller;

import com.codecool.shop.Main;
import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.dao.implementation.jdbc.OrderDaoJdbc;
import com.codecool.shop.order.InputField;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.user.PasswordStorage;
import com.codecool.shop.user.User;
import com.codecool.shop.utility.Email;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import static com.codecool.shop.Main.userDao;

import java.util.*;

public class AccountController {

    // METHODS RETURNING RAW RESPONSE

    public static ModelAndView renderRegistration(Request req, Response res) {

        Map<String, Object> params = new HashMap<>();
        params.put("user", new HashMap<>()); // required to be passed in to register.html
        params.put("balance", Main.balanceInUSD);

        return new ModelAndView(params, "register");
    }

    public static ModelAndView renderLogin(Request req, Response res) {

        Map<String, Object> params = new HashMap<>();
        params.put("balance", Main.balanceInUSD);

        return new ModelAndView(params, "login");
    }

    public static ModelAndView renderOrderHistory(Request req, Response res) {

        int userId = getUserIdFromSession(req);

        List<HashMap<String, Object>> orders = getPaidOrders(userId);
        Map<String, Object> params = new HashMap<>();
        params.put("orders", orders);
        params.put("balance", Main.balanceInUSD);

        return new ModelAndView(params, "history");
    }

    public static ModelAndView renderProfile(Request req, Response res) {

        int userId = getUserIdFromSession(req);

        Map<String, Object> params = new HashMap<>();
        params.put("user", getUserData(userId));
        params.put("balance", Main.balanceInUSD);

        return new ModelAndView(params, "profile");
    }

    public static ModelAndView doRegistration(Request req, Response res) {


        Map<String, String> inputData = collectRegistrationData(req);
        List<String> errorMessages = validateRegistrationInput(inputData);

        if (errorMessages.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("balance", Main.balanceInUSD);
            params.put("error-messages", errorMessages);
            params.put("user", inputData);
            return new ModelAndView(params, "register");
        }

        String hashedPasswordAndSalt;
        try {
            hashedPasswordAndSalt = PasswordStorage.createHash(inputData.get("password"));
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Map<String, Object> params = new HashMap<>();
            params.put("balance", Main.balanceInUSD);
            params.put("error-messages", new ArrayList<>(Arrays.asList("Cannot save account, try again later.")));
            params.put("user", inputData);
            return new ModelAndView(params, "register");
        }

        User user = User.create(inputData.get("fullname"), inputData.get("email"), hashedPasswordAndSalt);
        userDao.add(user);

        String to = user.getEmail();
        String body = Email.renderEmailTemplate("welcome_email",
                new HashMap<String, Object>(){{ put("user", user); }});
        String subject = "Welcome " + user.getFullName() + " in the 4loop Shop!";
        Email.send(to, body, subject);

        res.redirect("/");
        return null;
    }

    public static ModelAndView doLogin(Request req, Response res) {

        Map<String, String> inputData = collectLoginData(req);

        int userId = validateLoginCredentials(inputData);
        if (userId == -1) {
            Map<String, Object> params = new HashMap<>();
            params.put("balance", Main.balanceInUSD);
            params.put("error-messages", new ArrayList<>(Arrays.asList("Invalid credentials.")));
            return new ModelAndView(params, "login");
        }

        req.session().attribute("user_id", userId);
        res.redirect("/");
        return null;
    }

    public static ModelAndView doLogout(Request req, Response res) {

        req.session().removeAttribute("user_id");
        res.redirect("/");

        return null;
    }

    public static ModelAndView editProfile(Request req, Response res) {

        Map<String, String> profileInput = collectEditData(req);
        List<String> errorMessages = validateProfileInput(profileInput);

        if (errorMessages.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("balance", Main.balanceInUSD);
            params.put("error-messages", errorMessages);
            params.put("user", profileInput);
            return new ModelAndView(params, "profile");
        }

        int userId = getUserIdFromSession(req);
        userDao.update(userId, profileInput);
        res.redirect("/profile");
        return null;
    }

    // ACCESSORY METHODS

    private static int getUserIdFromSession(Request req) {
        return Integer.parseInt(req.session().attribute("user_id"));
    }

    private static Map<String, String> getUserData(int userId) {

        User user = userDao.find(userId);
        Map<String, String> modUser = new HashMap<>();
        modUser.put("phone", user.getPhone());

        modUser.put("billcountry", user.getBillingCountry());
        modUser.put("billcity", user.getBillingCity());
        modUser.put("billzip", user.getBillingZipCode());
        modUser.put("billaddress", user.getBillingAddress());

        modUser.put("shipcountry", user.getShippingCountry());
        modUser.put("shipcity", user.getShippingCity());
        modUser.put("shipzip", user.getShippingZipCode());
        modUser.put("shipaddress", user.getShippingAddress());

        return modUser;
    }

    private static List<HashMap<String, Object>> getPaidOrders(int userId) {

        // Or simply return orderDao.getAllPaidOrders(userId)... will see!

        OrderDao orderDao = new OrderDaoJdbc();
        List<Order> ordersData = orderDao.getAllPaid(userId);

        List<HashMap<String, Object>> orders = new ArrayList<>();
        for (Order o : ordersData) {
            HashMap<String, Object> order = new HashMap<>();
            order.put("id", o.getId());
            order.put("closed-date", o.getClosedDate());

            List<HashMap<String, Object>> transformedLineItems = new ArrayList<>();
            List<LineItem> lineItems = o.getItems();
            for (LineItem li : lineItems) {
                HashMap<String, Object> lineItem = new HashMap<>();
                lineItem.put("productImageFileName", li.getProductImage());
                lineItem.put("productName", li.getProductName());
                lineItem.put("quantity", li.getQuantity());
                lineItem.put("actualPrice", li.getActualPrice());
                lineItem.put("subTotal", li.getSubTotalPrice());
                transformedLineItems.add(lineItem);
            }

            order.put("items", transformedLineItems);
            order.put("grandTotal", o.getTotalPrice());
            orders.add(order);
        }

        return orders;
    }

    private static Map<String, String> collectRegistrationData(Request req) {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("fullname", req.queryParams("fullname"));
        registrationData.put("email", req.queryParams("email"));
        registrationData.put("password1", req.queryParams("password1"));
        registrationData.put("password2", req.queryParams("password2"));
        return registrationData;
    }

    private static List<String> validateRegistrationInput(Map<String, String> regInput) {
        List<String> errorMessages = new ArrayList<>();
        if (!InputField.FULL_NAME.validate(regInput.get("fullname"))) {
            errorMessages.add("Full name field is wrong.");
        }
        if (!InputField.EMAIL.validate(regInput.get("email"))) {
            errorMessages.add("E-mail field is wrong.");
        } else if (userDao.getUserEmails().contains(regInput.get("email"))) {
            errorMessages.add("E-mail field is already registered.");
        }
        if (!regInput.get("password1").equals(regInput.get("password2"))) {
            errorMessages.add("Passwords are not matching.");
        } else if (!InputField.PASSWORD.validate(regInput.get("password1")) ||
                !InputField.PASSWORD.validate(regInput.get("password2"))){
            errorMessages.add("Password incorrect. Has to be 4-8 characters long and use regular characters.");
        }
        return errorMessages;

    }

    private static Map<String, String> collectLoginData(Request req) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", req.queryParams("email"));
        loginData.put("password", req.queryParams("password"));
        return loginData;
    }

    private static int validateLoginCredentials(Map<String, String> loginInput) {
        String email = loginInput.get("email");
        String password = loginInput.get("password");
        User user = userDao.find(email);
        if (user == null) {
            return -1;
        }
        String hash = user.getPassword();

        boolean validCredentials;
        try {
            validCredentials = PasswordStorage.verifyPassword(password, hash);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            System.out.println("Cannot perform operation.");
            return -1;
        } catch (PasswordStorage.InvalidHashException e) {
            return -1;
        }
        return validCredentials ? user.getId() : -1;
    }

    private static Map<String, String> collectEditData(Request req) {
        Map<String, String> profileInfo = new HashMap<>();
        profileInfo.put("phone", req.queryParams("phone"));
        profileInfo.put("billcountry", req.queryParams("bill-country"));
        profileInfo.put("billcity", req.queryParams("bill-city"));
        profileInfo.put("billzip", req.queryParams("bill-zip"));
        profileInfo.put("billaddress", req.queryParams("bill-address"));
        profileInfo.put("shipcountry", req.queryParams("ship-country"));
        profileInfo.put("shipcity", req.queryParams("ship-city"));
        profileInfo.put("shipzip", req.queryParams("ship-zip"));
        profileInfo.put("shipaddress", req.queryParams("ship-address"));
        return profileInfo;
    }

    private static List<String> validateProfileInput(Map<String, String> profileInput) {
        List<String> errorMessages = new ArrayList<>();
        if (!InputField.PHONE.validate(profileInput.get("phone"))) {
            errorMessages.add("Phone field is invalid.");
        }
        if (!InputField.COUNTRY.validate(profileInput.get("billcountry"))) {
            errorMessages.add("Invalid billing country");
        }
        if (!InputField.CITY.validate(profileInput.get("billcity"))) {
            errorMessages.add("Invalid billing city");
        }
        if (!InputField.ZIP_CODE.validate(profileInput.get("billzip"))) {
            errorMessages.add("Invalid billing ZIP code");
        }
        if (!InputField.ADDRESS.validate(profileInput.get("billaddress"))) {
            errorMessages.add("Invalid billing address");
        }
        if (!InputField.COUNTRY.validate(profileInput.get("shipcountry"))) {
            errorMessages.add("Invalid shipping country");
        }
        if (!InputField.CITY.validate(profileInput.get("shipcity"))) {
            errorMessages.add("Invalid shipping city");
        }
        if (!InputField.ZIP_CODE.validate(profileInput.get("shipzip"))) {
            errorMessages.add("Invalid shipping ZIP code");
        }
        if (!InputField.ADDRESS.validate(profileInput.get("shipaddress"))) {
            errorMessages.add("Invalid shipping address");
        }
        return errorMessages;
    }

}
