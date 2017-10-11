package com.codecool.shop.dao;

import com.codecool.shop.Config;
import com.codecool.shop.dao.implementation.jdbc.OrderDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.SupplierDaoJdbc;
import com.codecool.shop.dao.implementation.memory.OrderDaoMem;
import com.codecool.shop.dao.implementation.memory.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.memory.SupplierDaoMem;

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
        return new UserDaomem();
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

}
