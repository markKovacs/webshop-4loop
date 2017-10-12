package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.order.LineItem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import com.codecool.shop.utility.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class OrderDaoJdbc implements OrderDao {


    @Override
    public Order createNewOrder(int userId) {

        // TODO: update log_filename!!!

        Order order = null;

        String query = "INSERT INTO orders (user_id) " +
                       "VALUES (?)";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query, OptionalInt.of(Statement.RETURN_GENERATED_KEYS))
        ){
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed!");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()
            ){
                if (generatedKeys.next()) {
                    int lastInsertId = generatedKeys.getInt(1);
                    String orderLogFilename = Log.getNowAsString() + "_" + lastInsertId + "_order";
                    order = new Order(lastInsertId, userId, orderLogFilename);
                } else {
                    throw new SQLException("Creating order failed! No id returned!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    /*@Override
    public void add(Order order) {

    }*/

    @Override
    public Order findByID(int id) {
        return null;
    }

    @Override
    public Order findOpenByUserId(int userId) {

        Order order = null;

        String query = "SELECT o.id order_id," +
                       "       o.user_id," +
                       "       o.closed_date," +
                       "       o.status order_status," +
                       "       o.log_filename orderlog_filename," +
                       "       u.name user_name," +
                       "       u.phone_number," +
                       "       u.email," +
                       "       l.product_id," +
                       "       l.quantity," +
                       "       l.actual_price," +
                       "       l.currency," +
                       "       p.name product_name," +
                       "       p.image_filename," +
                       "       COALESCE(o.billing_country, u.billing_country) billing_country," +
                       "       COALESCE(o.billing_city, u.billing_city) billing_city," +
                       "       COALESCE(o.billing_zip, u.billing_zip) billing_zip," +
                       "       COALESCE(o.billing_address, u.billing_address) billing_address," +
                       "       COALESCE(o.shipping_country, u.shipping_country) shipping_country," +
                       "       COALESCE(o.shipping_city, u.shipping_city) shipping_city," +
                       "       COALESCE(o.shipping_zip, u.shipping_zip) shipping_zip," +
                       "       COALESCE(o.shipping_address, u.shipping_address) shipping_address " +
                       "FROM orders o " +
                       "JOIN users u ON u.id = o.user_id " +
                       "JOIN lineitems l ON o.id = l.order_id " +
                       "JOIN products p ON p.id = l.Product_id " +
                       "WHERE o.user_id = ? AND o.status != 'paid' AND o.deleted != 1;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query.trim())
        ) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            List<LineItem> items = new ArrayList<>();
            float totalPrice = 0;
            Status status;

            while (resultSet.next()) {

                int quantity = resultSet.getInt("quantity");
                float actualPrice = resultSet.getFloat("actual_price");
                totalPrice += quantity * actualPrice;

                items.add(new LineItem(
                    resultSet.getInt("order_id"),
                    resultSet.getInt("product_id"),
                    resultSet.getString("product_name"),
                    resultSet.getString("image_filename"),
                    quantity,
                    actualPrice,
                    Currency.getInstance(resultSet.getString("currency"))
                ));

                if (resultSet.last()) {

                    switch (resultSet.getString("order_status")) {
                        case "reviewed":
                            status = Status.REVIEWED;
                            break;
                        case "checked":
                            status = Status.CHECKEDOUT;
                            break;
                        case "paid":
                            status = Status.PAID;
                            break;
                        default:
                            status = Status.NEW;
                            break;
                    }

                    order = new Order(
                        resultSet.getInt("order_id"),
                        userId,
                        status,
                        items,
                        resultSet.getDate("closed_date"),
                        totalPrice,
                        resultSet.getString("user_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("billing_country"),
                        resultSet.getString("billing_city"),
                        resultSet.getString("billing_zip"),
                        resultSet.getString("billing_address"),
                        resultSet.getString("shipping_country"),
                        resultSet.getString("shipping_city"),
                        resultSet.getString("shipping_zip"),
                        resultSet.getString("shipping_address"),
                        resultSet.getString("orderlog_filename")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }


    @Override
    public void remove(int id) {

    }


    @Override
    public List<Order> getAll() {
        return null;
    }


    @Override
    public List<Order> getBy(Status status) {
        return null;
    }


    @Override
    public List<Order> getAllPaid(int userId) {
        return null;
    }

    @Override
    public void addLineItemToOrder(Order order, Product product, int quantity) {

        String query = "INSERT INTO lineitems (product_id, order_id, quantity, actual_price, currency) " +
                       "VALUES (?, ?, ?, ?, ?);";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query.trim())
        ) {
            stmt.setInt(1, product.getId());
            stmt.setInt(2, order.getId());
            stmt.setInt(3, quantity);
            stmt.setFloat(4, product.getDefaultPrice());
            stmt.setString(5, product.getDefaultCurrency().toString());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0){
                System.out.println("New product added to database.");
            } else {
                System.out.println("New product addition failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateLineItemInOrder(Order order, Product product, int quantity) {

        String query = "UPDATE lineitems " +
                       "SET quantity = ? " +
                       "WHERE product_id = ? AND order_id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, product.getId());
            stmt.setInt(3, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0){
                System.out.println("New product added to database.");
            } else {
                System.out.println("New product addition failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LineItem findLineItemInCart(int productId, Order order) {

        String query = "SELECT l.product_id," +
                       "       l.order_id," +
                       "       l.quantity," +
                       "       l.actual_price," +
                       "       l.currency," +
                       "       p.name product_name," +
                       "       p.image_filename " +
                       "FROM lineitems l " +
                       "JOIN products p ON p.id = l.product_id " +
                       "WHERE order_id = ? AND product_id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query.trim())
        ){
            stmt.setInt(1, order.getId());
            stmt.setInt(2, productId);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return new LineItem(
                        order.getId(),
                        productId,
                        resultSet.getString("product_name"),
                        resultSet.getString("image_filename"),
                        resultSet.getInt("quantity"),
                        resultSet.getFloat("actual_price"),
                        Currency.getInstance(resultSet.getString("currency"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addLineItemToCart(LineItem lineItem, Order order) {

        String query = "INSERT INTO lineitems (product_id, order_id, quantity, actual_price, currency, product_name, image_filename) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, lineItem.getProductId());
            stmt.setInt(2, order.getId());
            stmt.setInt(3, lineItem.getQuantity());
            stmt.setFloat(4, lineItem.getActualPrice());
            stmt.setString(5, lineItem.getCurrency().toString());
            stmt.setString(6, lineItem.getProductName());
            stmt.setString(7, lineItem.getProductImage());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void changeQuantity(LineItem lineItem, int quantity) {
        String query = "UPDATE lineitems " +
                       "SET quantity = (quantity + ?) " +
                       "WHERE product_id= ? AND order_id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, lineItem.getProductId());
            stmt.setInt(3, lineItem.getOrderId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /*@Override
    public void add(Order order) {

        String query = "INSERT INTO orders (user_id) VALUES (?);";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ){
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getDescription());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("New supplier added to database.");
            } else {
                System.out.println("New supplier addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

}
