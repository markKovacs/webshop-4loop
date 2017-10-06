package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.OrderDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.utility.Log;

import java.util.ArrayList;
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
    private Status status;
    private List<LineItem> items;

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

    public Order() {
        this.items = new ArrayList<>();
        this.status = Status.NEW;

        int temporaryId = OrderDaoMem.getInstance().getAll().size();
        this.orderLogFilename = Log.getNowAsString() + "_" + temporaryId + "_order";
    }

    public String addToCart(int productId, int quantity) {
        Product product = ProductDaoMem.getInstance().find(productId);

        if (product == null || quantity < 1 || quantity > 99) {
            return "invalid_params";
        }

        LineItem lineItemToAdd = findLineItem(product);
        if (lineItemToAdd == null) {
            lineItemToAdd = new LineItem(product, quantity, product.getDefaultPrice());
            items.add(lineItemToAdd);
            updateTotal();
            return "new_item";
        }

        lineItemToAdd.changeQuantity(quantity);
        updateTotal();

        return "quantity_change";
    }

    public float changeProductQuantity(int productId, int quantity) {
        Product product = ProductDaoMem.getInstance().find(productId);
        if (product == null || quantity < 0 || quantity > 99) {
            return -1f;
        }
        LineItem lineItem = findLineItem(product);
        if (lineItem == null) {
            return -1f;
        }

        float newSubtotal = lineItem.changeQuantityToValue(quantity);
        updateTotal();
        return newSubtotal;
    }

    public void updateTotal() {
        float total = 0.0f;
        for (LineItem lineItem : items) {
            total += lineItem.getSubTotalPrice();
        }
        this.totalPrice = total;
    }

    private LineItem findLineItem(Product product) {
        return items.stream().filter(t -> t.getProduct().equals(product)).findFirst().orElse(null);
    }

    public int countCartItems() {
        return items.size();
    }

    public boolean setCheckoutInfo(Map<String, String> inputValues) {
        if (allInputValuesAreValid(inputValues)) {
            this.fullName = inputValues.get("username");
            this.email = inputValues.get("email");
            this.phone = inputValues.get("phone");
            this.billingCountry = inputValues.get("billcountry");
            this.billingCity = inputValues.get("billcity");
            this.billingZipCode = inputValues.get("billzip");
            this.billingAddress = inputValues.get("billaddress");
            this.shippingCountry = inputValues.get("shipcountry");
            this.shippingCity = inputValues.get("shipcity");
            this.shippingZipCode = inputValues.get("shipzip");
            this.shippingAddress = inputValues.get("shipaddress");
            return true;
        }
        return false;
    }

    private static boolean allInputValuesAreValid(Map<String, String> inputValues) {
        if (InputField.FULL_NAME.validate(inputValues.get("username")) &&
                InputField.EMAIL.validate(inputValues.get("email")) &&
                InputField.PHONE.validate(inputValues.get("phone")) &&
                InputField.COUNTRY.validate(inputValues.get("billcountry")) &&
                InputField.CITY.validate(inputValues.get("billcity")) &&
                InputField.ZIP_CODE.validate(inputValues.get("billzip")) &&
                InputField.ADDRESS.validate(inputValues.get("billaddress")) &&
                InputField.COUNTRY.validate(inputValues.get("shipcountry")) &&
                InputField.CITY.validate(inputValues.get("shipcity")) &&
                InputField.ZIP_CODE.validate(inputValues.get("shipzip")) &&
                InputField.ADDRESS.validate(inputValues.get("shipaddress"))) {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", items=" + items +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", billingCountry='" + billingCountry + '\'' +
                ", billingCity='" + billingCity + '\'' +
                ", billingZipCode='" + billingZipCode + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", shippingCountry='" + shippingCountry + '\'' +
                ", shippingCity='" + shippingCity + '\'' +
                ", shippingZipCode='" + shippingZipCode + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }

    public String getOrderLogFilename() {
        return orderLogFilename;
    }


}
