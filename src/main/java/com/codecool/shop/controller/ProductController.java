package com.codecool.shop.controller;

import com.codecool.shop.Config;
import com.codecool.shop.Main;
import com.codecool.shop.OrderUtils;
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
    // TODO: modify DAO instantiation

    public static ModelAndView renderProducts(Request req, Response res) {
        // ProductDao productData = ProductDaoMem.getInstance();
        ProductDaoJdbc productData = new ProductDaoJdbc();

        ProductCategory productCategory = getSelectedProductCategory(req);
        Supplier supplier = getSelectedSupplier(req);
        List<Product> products;
        String selected;

        if (productCategory != null) {
            // products = productData.getBy(productCategory);
            products = productData.getBy(productCategory);
            selected = productCategory.getName();
        } else if (supplier != null) {
            // products = productData.getBy(supplier);
            products = productData.getBy(supplier);
            selected = supplier.getName();
        } else {
            //products = productData.getBy(Config.DEFAULT_CATEGORY);
            products = productData.getBy(Config.DEFAULT_CATEGORY);
            selected = Config.DEFAULT_CATEGORY.getName();
        }

        Map<String, Object> params = new HashMap<>();
        // params.put("allCategories", ProductCategoryDaoMem.getInstance().getAll());
        ProductCategoryDaoJdbc productCategoryDaoJdbc = new ProductCategoryDaoJdbc();
        params.put("allCategories", productCategoryDaoJdbc.getAll());
        // params.put("allSuppliers", SupplierDaoMem.getInstance().getAll());
        SupplierDaoJdbc supplierDaoJdbc = new SupplierDaoJdbc();
        params.put("allSuppliers", supplierDaoJdbc.getAll());
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
            // selectedProductCategory = ProductCategoryDaoMem.getInstance().find(categoryId);
            ProductCategoryDaoJdbc getCategory = new ProductCategoryDaoJdbc();
            selectedProductCategory = getCategory.find(categoryId);
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
            // selectedSupplier = SupplierDaoMem.getInstance().find(supplierId);
            SupplierDaoJdbc getSupplier = new SupplierDaoJdbc();
            selectedSupplier = getSupplier.find(supplierId);

        }
        return selectedSupplier;
    }

}
