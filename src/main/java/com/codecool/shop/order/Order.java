package com.codecool.shop.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {
    /**
     * all LineItem's actualPrice need to be set when clicked on "proceed to payment"
     * all LineItem's actualPrice need to be compared with products current prices and throw error if any changes occured
     * otherwise, if sufficient funds are on balance, then success
     *
     * NOT Singleton Pattern, rather create new and store in OrderDaoMem
     *
     * Order need more fields:
     *   - Name
     *   - Email
     *   - Phone number
     *   - Billing Address (Country, City, Zipcode, Address)
     *   - Shipping Address (Country, City, Zipcode, Address)
     */

    private static Order currentOrder = null;

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

    private Order() {
        this.items = new ArrayList<>();
        this.status = Status.NEW;
    }

    public static Order getInstance() {
        if (currentOrder == null) {
            currentOrder = new Order();
        }
        return currentOrder;
    }

    public boolean setCheckoutInfo(Map<String, String> inputValues) {
        // TODO: if all input parameters are valid -> set fields, else return false and ask them again

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
}
