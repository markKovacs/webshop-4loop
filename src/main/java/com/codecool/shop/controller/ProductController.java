package com.codecool.shop.controller;

import com.codecool.shop.Config;
import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.jdbc.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.jdbc.SupplierDaoJdbc;
import com.codecool.shop.dao.implementation.memory.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.memory.ProductDaoMem;
import com.codecool.shop.dao.implementation.memory.SupplierDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.order.Order;
import spark.Request;
import spark.Response;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductController {

    public static ModelAndView renderProducts(Request req, Response res) {
        ProductDao productDao = DaoFactory.getProductDao();
        ProductCategory productCategory = getSelectedProductCategory(req);
        Supplier supplier = getSelectedSupplier(req);
        List<Product> products;
        String selected;

        if (productCategory != null) {
            products = productDao.getBy(productCategory);
            selected = productCategory.getName();
        } else if (supplier != null) {
            products = productDao.getBy(supplier);
            selected = supplier.getName();
        } else {
            products = productDao.getBy(Config.DEFAULT_CATEGORY);
            selected = Config.DEFAULT_CATEGORY.getName();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("allCategories", DaoFactory.getProductCategoryDao().getAll());
        params.put("allSuppliers", DaoFactory.getSupplierDao().getAll());
        params.put("actualSelection", selected);
        params.put("products", products);
        params.put("balance", String.format("%.2f", Main.balanceInUSD));

        Order order = OrderUtils.getOrderFromSessionInfo(req);
        int cartItems = order != null ? order.countCartItems() : 0;
        params.put("cartItems", cartItems);

        return new ModelAndView(params, "index");
    }

    private static ProductCategory getSelectedProductCategory(Request req) {
        int categoryId = -1;
        try {
            categoryId = Integer.parseInt(req.queryParams("category-id"));
        } catch(NumberFormatException e) {
            // No usable category id
        }
        ProductCategory selectedProductCategory = null;
        if (categoryId != -1) {
            selectedProductCategory = DaoFactory.getProductCategoryDao().find(categoryId);
        }
        return selectedProductCategory;
    }

    private static Supplier getSelectedSupplier(Request req) {
        int supplierId = -1;
        try {
            supplierId = Integer.parseInt(req.queryParams("supplier-id"));
        } catch(NumberFormatException e) {
            // No usable supplier id
        }
        Supplier selectedSupplier = null;
        if (supplierId != -1) {
            selectedSupplier = DaoFactory.getSupplierDao().find(supplierId);
        }
        return selectedSupplier;
    }

}
