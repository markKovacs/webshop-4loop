package com.codecool.shop.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order {
    /**
     * all LineItem's actualPrice need to be set when clicked on "proceed to payment"
     * all LineItem's actualPrice need to be compared with products current prices and throw error if any changes occured
     * otherwise, if sufficient funds are on balance, then success
     */

    // Basic fields
    private int id;
    private int userId;
    private Status status;
    private List<LineItem> items;
    private Date closedDate;

    // Checkout (billing/shipping) information
    private float totalPrice;
    private String fullName;
    private String email;
    private String phone;
    private String billingCountry;
    private String billingCity;
    private String billingZipCode;
    private String billingAddress;
    private String shippingCountry;
    private String shippingCity;
    private String shippingZipCode;
    private String shippingAddress;
    private String orderLogFilename;

    public Order(int id, int userId, String orderLogFileName) {
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>();
        this.status = Status.NEW;
        this.orderLogFilename = orderLogFileName;
    }

    public Order(int id, int userId, Status status, List<LineItem> items, Date closedDate, float totalPrice, String fullName, String email,
                 String phone, String billingCountry, String billingCity, String billingZipCode, String billingAddress,
                 String shippingCountry, String shippingCity, String shippingZipCode, String shippingAddress, String orderLogFilename) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = items;
        this.closedDate = closedDate;
        this.totalPrice = totalPrice;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.billingCountry = billingCountry;
        this.billingCity = billingCity;
        this.billingZipCode = billingZipCode;
        this.billingAddress = billingAddress;
        this.shippingCountry = shippingCountry;
        this.shippingCity = shippingCity;
        this.shippingZipCode = shippingZipCode;
        this.shippingAddress = shippingAddress;
        this.orderLogFilename = orderLogFilename;
    }

    public void updateTotal() {
        float total = 0.0f;
        for (LineItem lineItem : items) {
            total += lineItem.getSubTotalPrice();
        }
        this.totalPrice = total;
    }

    public int countCartItems() {
        return items.size();
    }

    public boolean setCheckoutInfo(Map<String, String> inputValues) {
        if (allInputValuesAreValid(inputValues)) {
            this.fullName = inputValues.get("fullName");
            this.email = inputValues.get("email");
            this.phone = inputValues.get("phone");
            this.billingCountry = inputValues.get("billingCountry");
            this.billingCity = inputValues.get("billingCity");
            this.billingZipCode = inputValues.get("billingZipCode");
            this.billingAddress = inputValues.get("billingAddress");
            this.shippingCountry = inputValues.get("shippingCountry");
            this.shippingCity = inputValues.get("shippingCity");
            this.shippingZipCode = inputValues.get("shippingZipCode");
            this.shippingAddress = inputValues.get("shippingAddress");
            return true;
        }
        return false;
    }

    private static boolean allInputValuesAreValid(Map<String, String> inputValues) {
        if (InputField.FULL_NAME.validate(inputValues.get("fullName")) &&
                InputField.EMAIL.validate(inputValues.get("email")) &&
                InputField.PHONE.validate(inputValues.get("phone")) &&
                InputField.COUNTRY.validate(inputValues.get("billingCountry")) &&
                InputField.CITY.validate(inputValues.get("billingCity")) &&
                InputField.ZIP_CODE.validate(inputValues.get("billingZipCode")) &&
                InputField.ADDRESS.validate(inputValues.get("billingAddress")) &&
                InputField.COUNTRY.validate(inputValues.get("shippingCountry")) &&
                InputField.CITY.validate(inputValues.get("shippingCity")) &&
                InputField.ZIP_CODE.validate(inputValues.get("shippingZipCode")) &&
                InputField.ADDRESS.validate(inputValues.get("shippingAddress"))) {
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        System.out.println("Order status set from " + this.status + " to " + status + ".");
        this.status = status;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
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

    public Date getClosedDate() {
        return closedDate;
    }

    public String getClosedDateString() {
        if (closedDate == null) {
            return "N/A";
        }
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(closedDate);
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    @Override
    public String toString() {
        String itemsStringified = "[";
        for (LineItem item : items) {
            itemsStringified += '\n' + item.toString();
        }
        itemsStringified += "\n]";

        return "{\n" +
                "\"id\": " + id + ",\n" +
                "\"status\": \"" + status + "\",\n" +
                "\"items\": " + itemsStringified + ",\n" +
                "\"fullName\": \"" + fullName + "\",\n" +
                "\"email\": \"" + email + "\",\n" +
                "\"email\": \"" + phone + "\",\n" +
                "\"billingCountry\": \"" + billingCountry + "\",\n" +
                "\"billingCity\": \"" + billingCity + "\",\n" +
                "\"billingZipCode\": \"" + billingZipCode + "\",\n" +
                "\"billingAddress\": \"" + billingAddress + "\",\n" +
                "\"shippingCountry\": \"" + shippingCountry + "\",\n" +
                "\"shippingCity\": \"" + shippingCity + "\",\n" +
                "\"shippingZipCode\": \"" + shippingZipCode + "\",\n" +
                "\"shippingAddress\": \"" + shippingAddress + "\"\n" +
                '}';
    }

    public String getOrderLogFilename() {
        return orderLogFilename;
    }

    public int getUserId() {
        return userId;
    }

}
