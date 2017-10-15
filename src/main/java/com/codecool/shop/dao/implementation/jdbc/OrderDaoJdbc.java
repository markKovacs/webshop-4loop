package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.OrderDao;
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
                    updateLogFileName(order);
                } else {
                    throw new SQLException("Creating order failed! No id returned!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    private static void updateLogFileName(Order order) {
        String query = "UPDATE orders SET log_filename = ?" +
                "       WHERE id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setString(1, order.getOrderLogFilename());
            stmt.setInt(2, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0){
                System.out.println("Log filename saved to database.");
            } else {
                System.out.println("Log filename saving failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findByID(int id) {
        return null;
    }

    @Override
    public Order findOpenByUserId(int userId) {

        String query = "SELECT o.id order_id," +
                       "       o.user_id," +
                       "       o.closed_date," +
                       "       o.status order_status," +
                       "       o.log_filename orderlog_filename," +
                       "       COALESCE(o.billing_name, u.name) user_name," +
                       "       COALESCE(o.billing_phone, u.phone_number) phone_number," +
                       "       COALESCE(o.billing_email, u.email) email," +
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
                       "LEFT JOIN users u ON u.id = o.user_id " +
                       "LEFT JOIN lineitems l ON o.id = l.order_id " +
                       "LEFT JOIN products p ON p.id = l.product_id " +
                       "WHERE o.user_id = ? AND o.status IS NULL OR o.status IN ('reviewed', 'checked') AND o.deleted != 1;";

        Order order = null;

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query.trim())
        ) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            List<LineItem> items = new ArrayList<>();
            float totalPrice = 0;
            Status status = null;

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

                if (resultSet.isLast()) {

                    String statusStr = resultSet.getString("order_status");
                    if (statusStr == null) {
                        statusStr = "new";
                    }

                    switch (statusStr) {
                        case "reviewed":
                            status = Status.REVIEWED;
                            break;
                        case "checked":
                            status = Status.CHECKEDOUT;
                            break;
                        case "paid":
                            status = Status.PAID;
                            break;
                        case "new":
                            status = Status.NEW;
                            break;
                    }

                    order = new Order(
                        resultSet.getInt("order_id"),
                        userId,
                        status,
                        items,
                        resultSet.getTimestamp("closed_date"),
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
    public void removeLineItemFromCart(int productId, Order order) {
        String query = "DELETE FROM lineitems " +
                       "WHERE product_id = ? AND order_id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, productId);
            stmt.setInt(2, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting lineitem failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getAll() {
        return null;
    }

    @Override
    public List<Order> getAllPaid(int userId) {
        List<Order> paidOrders = new ArrayList<>();
        String query =  "SELECT o.id order_id," +
                "       o.user_id," +
                "       o.closed_date," +
                "       o.status order_status," +
                "       o.log_filename orderlog_filename," +
                "       o.billing_name user_name," +
                "       o.billing_phone phone_number," +
                "       o.billing_email email," +
                "       l.product_id," +
                "       l.quantity," +
                "       l.actual_price," +
                "       l.currency," +
                "       p.name product_name," +
                "       p.image_filename," +
                "       o.billing_country billing_country," +
                "       o.billing_city billing_city," +
                "       o.billing_zip billing_zip," +
                "       o.billing_address billing_address," +
                "       o.shipping_country shipping_country," +
                "       o.shipping_city shipping_city," +
                "       o.shipping_zip shipping_zip," +
                "       o.shipping_address shipping_address " +
                "FROM orders o " +
                "LEFT JOIN lineitems l ON o.id = l.order_id " +
                "LEFT JOIN products p ON p.id = l.product_id " +
                "WHERE o.user_id = ? AND o.status = 'paid' AND o.deleted != 1 " +
                "ORDER BY o.id DESC;";
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query.trim())
        ) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            int oldOrderId = -1;
            Order order = null;
            List<LineItem> lineItemsInOrder = null;

            while (resultSet.next()) {

                int actualOrderId = resultSet.getInt("order_id");

                if (oldOrderId != actualOrderId) {
                    if (order != null) {
                        order.updateTotal();
                        paidOrders.add(order);
                    }

                    lineItemsInOrder = new ArrayList<>();
                    order = new Order(
                            resultSet.getInt("order_id"),
                            userId,
                            Status.PAID,
                            lineItemsInOrder,
                            resultSet.getTimestamp("closed_date"),
                            0,
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

                    oldOrderId = actualOrderId;
                }

                lineItemsInOrder.add(new LineItem(
                        resultSet.getInt("order_id"),
                        resultSet.getInt("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("image_filename"),
                        resultSet.getInt("quantity"),
                        resultSet.getFloat("actual_price"),
                        Currency.getInstance(resultSet.getString("currency"))
                ));

                if (resultSet.isLast() && order != null) {
                    order.updateTotal();
                    paidOrders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paidOrders;
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

        String query = "INSERT INTO lineitems (product_id, order_id, quantity, actual_price, currency) " +
                       "VALUES (?, ?, ?, ?, ?);";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, lineItem.getProductId());
            stmt.setInt(2, order.getId());
            stmt.setInt(3, lineItem.getQuantity());
            stmt.setFloat(4, lineItem.getActualPrice());
            stmt.setString(5, lineItem.getCurrency().toString());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void increaseLineItemQuantity(Order order, LineItem lineItem, int quantity) {
        String query = "UPDATE lineitems " +
                       "SET quantity = quantity + ? " +
                       "WHERE product_id = ? AND order_id = ?;";
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, lineItem.getProductId());
            stmt.setInt(3, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Additional quantity to lineitem could not be added.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeQuantity(Order order, LineItem lineItem, int quantity) {
        String query = "UPDATE lineitems " +
                       "SET quantity = ? " +
                       "WHERE product_id = ? AND order_id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, lineItem.getProductId());
            stmt.setInt(3, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Quantity change of lineitem failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setStatus(Order order) {
        String query = "UPDATE orders SET status = CAST(? AS order_status) WHERE id = ?;";

        String status;
        switch (order.getStatus()) {
            case REVIEWED:
                status = "reviewed";
                break;
            case CHECKEDOUT:
                status = "checked";
                break;
            case PAID:
                status = "paid";
                break;
            default:
                status = null;
                break;
        }

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setString(1, status);
            stmt.setInt(2, order.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Order status updated in database to " + order.getStatus());
            } else {
                System.out.println("Order status update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeOrder(Order order) {
        String query = "UPDATE orders SET status = CAST('paid' AS order_status), " +
                "                         closed_date = ? " +
                "       WHERE id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
            stmt.setInt(2, order.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Order status updated in database to paid, closed date set.");
            } else {
                System.out.println("Order status update to paid has failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeZeroQuantityItems(Order order) {
        String query = "DELETE FROM lineitems " +
                "       WHERE order_id = ? AND quantity = 0;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, order.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Order has been cleaned from zero quantity items.");
            } else {
                System.out.println("No items had zero quantity.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCheckoutInfo(Order order) {

        String query =  "UPDATE orders " +
                        "SET billing_name = ?," +
                        "    billing_email = ?," +
                        "    billing_phone = ?," +
                        "    billing_country = ?," +
                        "    billing_city = ?," +
                        "    billing_zip = ?," +
                        "    billing_address = ?," +
                        "    shipping_country = ?," +
                        "    shipping_city = ?," +
                        "    shipping_zip = ?," +
                        "    shipping_address = ?" +
                        "WHERE id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setString(1, order.getFullName());
            stmt.setString(2, order.getEmail());
            stmt.setString(3, order.getPhone());
            stmt.setString(4, order.getBillingCountry());
            stmt.setString(5, order.getBillingCity());
            stmt.setString(6, order.getBillingZipCode());
            stmt.setString(7, order.getBillingAddress());
            stmt.setString(8, order.getShippingCountry());
            stmt.setString(9, order.getShippingCity());
            stmt.setString(10, order.getShippingZipCode());
            stmt.setString(11, order.getShippingAddress());
            stmt.setInt(12, order.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Checkout info updated successfully.");
            } else {
                System.out.println("An error occured when saving checkout info.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
