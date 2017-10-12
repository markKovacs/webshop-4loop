package com.codecool.shop.order;

import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.dao.implementation.memory.OrderDaoMem;
import com.codecool.shop.dao.implementation.memory.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.utility.Log;

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
        // TODO: implement setters
    }

    public String addToCart(int productId, int quantity) {

        // FIND PRODUCT BASED ON productId
        Product product = DaoFactory.getProductDao().find(productId);

        if (product == null || quantity < 1 || quantity > 99) {
            return "invalid_params";
        }

        // CHECK IF WE ALREADY HAVE THE LINEITEM IN OUR CART
        LineItem lineItemToAdd = DaoFactory.getOrderDao().findLineItemInCart(productId, this);

        // IF WE WANT TO ADD A NEW ITEM TO THE CART
        if (lineItemToAdd == null) {

            lineItemToAdd = new LineItem(
                    this.id,
                    productId,
                    product.getName(),
                    product.getImageFileName(),
                    quantity,
                    product.getDefaultPrice(),
                    product.getDefaultCurrency()
            );

            DaoFactory.getOrderDao().addLineItemToCart(lineItemToAdd, this);
            updateTotal();

            return "new_item";
        }

        // THE LINEITEM IS ALREADY IN THE CART, JUST UPDATE QUANTITY
        DaoFactory.getOrderDao().changeQuantity(lineItemToAdd);
        // lineItemToAdd.changeQuantity(quantity);
        updateTotal();

        return "quantity_change";
    }

    public float changeProductQuantity(int productId, int quantity) {
        Product product = DaoFactory.getProductDao().find(productId);
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

    public void removeLineItem(int productId) {
        LineItem foundLineItem = items.stream().filter(i -> i.getProductId() == productId).findFirst().orElse(null);
        items.remove(foundLineItem);
    }

    /*private LineItem findLineItem(Product product) {
        return items.stream().filter(t -> DaoFactory.getProductDao().find(t.getProductId())
                .equals(product)).findFirst().orElse(null);
    }*/


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
