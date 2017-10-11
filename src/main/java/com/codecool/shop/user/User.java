package com.codecool.shop.user;

import java.util.Map;

public class User {

    // Registration
    private int userId;
    private String fullName;
    private String email;
    private String password;

    // Profile
    private String phone;
    private String billingCountry;
    private String billingCity;
    private String billingZipCode;
    private String billingAddress;
    private String shippingCountry;
    private String shippingCity;
    private String shippingZipCode;
    private String shippingAddress;

    private User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    private User(String fullName, String email, String password,
                String phone, String billingCountry, String billingCity,
                String billingZipCode, String billingAddress, String shippingCountry,
                String shippingCity, String shippingZipCode, String shippingAddress) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.billingCountry = billingCountry;
        this.billingCity = billingCity;
        this.billingZipCode = billingZipCode;
        this.billingAddress = billingAddress;
        this.shippingCountry = shippingCountry;
        this.shippingCity = shippingCity;
        this.shippingZipCode = shippingZipCode;
        this.shippingAddress = shippingAddress;
    }

    public static User create(String fullName, String email, String password) {
        return new User(fullName, email, password);
    }

    public static User create(String fullName, String email, String password,
                 String phone, String billingCountry, String billingCity,
                 String billingZipCode, String billingAddress, String shippingCountry,
                 String shippingCity, String shippingZipCode, String shippingAddress) {
        return new User(fullName, email, password, phone, billingCountry, billingCity,
                billingZipCode, billingAddress, shippingCountry, shippingCity,
                shippingZipCode, shippingAddress);
    }

    public void setProfileInformation(Map<String, String> profileInput) {
        this.phone = profileInput.get("phone");
        this.billingCountry = profileInput.get("billcountry");
        this.billingCity = profileInput.get("billcity");
        this.billingZipCode = profileInput.get("billzip");
        this.billingAddress = profileInput.get("billaddress");
        this.shippingCountry = profileInput.get("shipcountry");
        this.shippingCity = profileInput.get("shipcity");
        this.shippingZipCode = profileInput.get("shipzip");
        this.shippingAddress = profileInput.get("shipaddress");
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public String getBillingZipCode() {
        return billingZipCode;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public String getShippingZipCode() {
        return shippingZipCode;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }
}
