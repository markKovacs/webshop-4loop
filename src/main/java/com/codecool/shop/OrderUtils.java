package com.codecool.shop;

import com.codecool.shop.dao.implementation.OrderDaoMem;
import com.codecool.shop.order.Order;
import com.codecool.shop.order.Status;
import spark.Request;

public class OrderUtils {


    public static Order getOrderFromSessionInfo(Request req) {
        int orderId = req.session().attribute("order_id") == null ? -1 :
                Integer.valueOf(req.session().attribute("order_id") + "");
        if (orderId != -1) {
            return OrderDaoMem.getInstance().find(orderId);
        } else {
            return null;
        }
    }

    public static void setOrderStatus(Request req) {
        Order order = getOrderFromSessionInfo(req);

        if (req.queryParams("back") != null && order != null) {
            System.out.println("STATUS SET BACK FROM: " + order.getStatus());
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
                System.out.println("STATUS SET FORWARD FROM: " + order.getStatus());
                order.setStatus(Status.REVIEWED);
            } else if (orderStatus.equals(Status.REVIEWED) && req.pathInfo().equals("/payment")) {
                System.out.println("STATUS SET FORWARD FROM: " + order.getStatus());
                order.setStatus(Status.CHECKEDOUT);
            }
        }
    }

}
