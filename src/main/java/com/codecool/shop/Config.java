package com.codecool.shop;

import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.ProductCategory;

public class Config {

    // Email settings
    public static final String EMAIL_FROM = "4loop@peberon.com";
    public static final String EMAIL_SENDER_HOST = "localhost";
    public static final String SMTP_HOST = "mail.peberon.com";
    public static final String SMTP_USER = "4loop@peberon.com";
    public static final String SMTP_PASS = "@xwfyT_f_4FN";
    public static final String SMTP_PORT = "465";

    // Log folder path - no slash at the end!
    public static final String ORDER_LOG_FOLDER = "src/main/orders";
    public static final String ADMIN_LOG_FOLDER = "src/main/logs";

    public static final ProductCategory DEFAULT_CATEGORY = ProductCategoryDaoMem.getInstance().find(1);

}
