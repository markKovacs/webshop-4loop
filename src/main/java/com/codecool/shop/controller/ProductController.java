package com.codecool.shop.controller;

import com.codecool.shop.Config;
import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.order.Order;
import spark.Request;
import spark.Response;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;

public class ProductController {

    public static ModelAndView renderProducts(Request req, Response res) {
        ProductCategoryDao categories = ProductCategoryDaoMem.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", categories.find(Config.DEFAULT_CATEGORY));
        params.put("products", ProductDaoMem.getInstance().getBy(categories.find(Config.DEFAULT_CATEGORY)));
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "index");
    }

    public static ModelAndView renderProductsBySupplier(Request req, Response res){
        int supplierId = Integer.parseInt(req.queryParams("supplier-id"));
        ProductDao productDataStore = ProductDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", supplierDataStore.find(supplierId));
        params.put("products", productDataStore.getBy(supplierDataStore.find(supplierId)));
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

    public static ModelAndView renderProductsByCategory(Request req, Response res){
        int categoryId = Integer.parseInt(req.queryParams("category-id"));
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        params.put("actualSelection", productCategoryDataStore.find(categoryId));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(categoryId)));
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);
        return new ModelAndView(params, "index");
    }

}
