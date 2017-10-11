package com.codecool.shop.dao;

import com.codecool.shop.Config;
import com.codecool.shop.dao.implementation.jdbc.*;
import com.codecool.shop.dao.implementation.memory.*;

public class DaoFactory {

    private DaoFactory() {
    }

    public static DaoFactory getInstance() {
        return new DaoFactory();
    }

    public static OrderDao getOrderDao() {
        if (Config.USE_DB) {
            return new OrderDaoJdbc();
        }
        return new OrderDaoMem();
    }

    public static UserDao getUserDao() {
        if (Config.USE_DB) {
            return new UserDaoJdbc();
        }
        return new UserDaoMem();
    }

    public static ProductCategoryDao getProductCategoryDao() {
        if (Config.USE_DB) {
            return new ProductCategoryDaoJdbc();
        }
        return new ProductCategoryDaoMem();
    }

    public static SupplierDao getSupplierDao() {
        if (Config.USE_DB) {
            return new SupplierDaoJdbc();
        }
        return new SupplierDaoMem();
    }

    public static ProductDao getProductDao() {
        if (Config.USE_DB) {
            return new ProductDaoJdbc();
        }
        return new ProductDaoMem();
    }

}
