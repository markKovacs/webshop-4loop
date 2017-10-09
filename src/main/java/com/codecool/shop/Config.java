package com.codecool.shop;

import com.codecool.shop.dao.implementation.memory.ProductCategoryDaoMem;
import com.codecool.shop.model.ProductCategory;

public class Config {

    // Email settings
    public static final String EMAIL_FROM = "";
    public static final String EMAIL_SENDER_HOST = "";
    public static final String SMTP_HOST = "";
    public static final String SMTP_USER = "";
    public static final String SMTP_PASS = "";
    public static final String SMTP_PORT = "";

    // Log folder path - no slash at the end!
    public static final String ORDER_LOG_FOLDER = "src/main/orders";
    public static final String ADMIN_LOG_FOLDER = "src/main/logs";

    public static final ProductCategory DEFAULT_CATEGORY = ProductCategoryDaoMem.getInstance().find(1);

    
    public static final String DB_TYPE = "";
    public static final String HOST = "";
    public static final String PORT = "";
    public static final String DB_NAME = "";
    public static final String USER = "";
    public static final String PASSWORD = "";


    public static final String TEST_DB_TYPE = "";
    public static final String TEST_HOST = "";
    public static final String TEST_PORT = "";
    public static final String TEST_DB_NAME = "";
    public static final String TEST_USER = "";
    public static final String TEST_PASSWORD = "";

}
