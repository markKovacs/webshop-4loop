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

    public static void setOrderStatus(Request req) {
        Order order = getOrderFromSessionInfo(req);

        if (req.queryParams("back") != null && order != null) {
            switch (order.getStatus()) {
                case CHECKEDOUT:
                    order.setStatus(Status.REVIEWED);
                    break;
                case REVIEWED:
                    order.setStatus(Status.NEW);
                    break;
            }
        } else if (order != null) {
            Status orderStatus = order.getStatus();
            if (orderStatus.equals(Status.NEW) && req.pathInfo().equals("/checkout")) {
                order.setStatus(Status.REVIEWED);
            } else if (orderStatus.equals(Status.REVIEWED) && req.pathInfo().equals("/payment")) {
                order.setStatus(Status.CHECKEDOUT);
            }
        }
    }

}
