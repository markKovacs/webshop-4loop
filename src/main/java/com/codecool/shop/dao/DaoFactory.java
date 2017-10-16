package com.codecool.shop.dao;

import com.codecool.shop.Config;
import com.codecool.shop.dao.implementation.jdbc.*;
import com.codecool.shop.dao.implementation.memory.*;

public class DaoFactory {

    private DaoFactory() {
    }

    public static OrderDao getOrderDao() {
        if (Config.USE_DB) {
            return new OrderDaoJdbc();
        }
        return OrderDaoMem.getInstance();
    }

    public static UserDao getUserDao() {
        if (Config.USE_DB) {
            return new UserDaoJdbc();
        }
        return UserDaoMem.getInstance();
    }

    public static ProductCategoryDao getProductCategoryDao() {
        if (Config.USE_DB) {
            return new ProductCategoryDaoJdbc();
        }
        return ProductCategoryDaoMem.getInstance();
    }

    public static SupplierDao getSupplierDao() {
        if (Config.USE_DB) {
            return new SupplierDaoJdbc();
        }
        return SupplierDaoMem.getInstance();
    }

    public static ProductDao getProductDao() {
        if (Config.USE_DB) {
            return new ProductDaoJdbc();
        }
        return ProductDaoMem.getInstance();
    }

}
