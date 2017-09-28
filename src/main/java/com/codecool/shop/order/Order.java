package com.codecool.shop.order;

import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;

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

    public Order() {
        this.items = new ArrayList<>();
        this.status = Status.NEW;
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

            return "new_item";
        }

        lineItemToAdd.changeQuantity(1);
        return "quantity_change";
    }

    private LineItem findLineItem(Product product) {
        return items.stream().filter(t -> t.getProduct().equals(product)).findFirst().orElse(null);
    }

    public int countCartItems() {
        return items.size();
    }

    public boolean setCheckoutInfo(Map<String, String> inputValues) {
        if (allInputValuesAreValid(inputValues)) {
            this.fullName = inputValues.get("fullname");
            this.email = inputValues.get("email");
            this.phone = inputValues.get("phone");
            this.billingCountry = inputValues.get("b-country");
            this.billingCity = inputValues.get("b-city");
            this.billingZipCode = inputValues.get("b-zipcode");
            this.billingAddress = inputValues.get("b-address");
            this.shippingCountry = inputValues.get("s-country");
            this.shippingCity = inputValues.get("s-city");
            this.shippingZipCode = inputValues.get("s-zipcode");
            this.shippingAddress = inputValues.get("s-address");
            return true;
        }
        return false;
    }

    private static boolean allInputValuesAreValid(Map<String, String> inputValues) {
        if (InputField.FULL_NAME.validate(inputValues.get("fullname")) &&
                InputField.EMAIL.validate(inputValues.get("email")) &&
                InputField.PHONE.validate(inputValues.get("phone")) &&
                InputField.COUNTRY.validate(inputValues.get("b-country")) &&
                InputField.CITY.validate(inputValues.get("b-city")) &&
                InputField.ZIP_CODE.validate(inputValues.get("b-zipcode")) &&
                InputField.ADDRESS.validate(inputValues.get("b-address")) &&
                InputField.COUNTRY.validate(inputValues.get("s-country")) &&
                InputField.CITY.validate(inputValues.get("s-city")) &&
                InputField.ZIP_CODE.validate(inputValues.get("s-zipcode")) &&
                InputField.ADDRESS.validate(inputValues.get("s-address"))) {
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

    public List<LineItem> getItems() {
        return items;
    }

}
