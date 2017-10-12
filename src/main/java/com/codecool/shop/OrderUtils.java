package com.codecool.shop;

import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import spark.Request;

public class OrderUtils {

    public static Order getOrderFromSessionInfo(Request req) {
        int orderId = req.session().attribute("order_id") == null ? -1 :
                Integer.valueOf(req.session().attribute("order_id") + "");
        if (orderId != -1) {
            return DaoFactory.getOrderDao().findByID(orderId);
        } else {
            return null;
        }
    }

    public static Order setOrderStatus(Request req) {

        int userId = req.session().attribute("user_id");
        Order order = DaoFactory.getOrderDao().findOpenByUserId(userId);

        if (order == null) {
            return null;
        }

        if (req.queryParams("back") != null) {
            switch (order.getStatus()) {
                case CHECKEDOUT:
                    order.setStatus(Status.REVIEWED);
                    break;
                case REVIEWED:
                    order.setStatus(Status.NEW);
                    break;
            }
        } else {
            Status orderStatus = order.getStatus();
            if (orderStatus.equals(Status.NEW) && req.pathInfo().equals("/checkout")) {
                order.setStatus(Status.REVIEWED);
            } else if (orderStatus.equals(Status.REVIEWED) && req.pathInfo().equals("/payment")) {
                order.setStatus(Status.CHECKEDOUT);
            }
        }

        DaoFactory.getOrderDao().setStatus(order);
        return order;
    }

}
