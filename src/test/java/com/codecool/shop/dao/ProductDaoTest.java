package com.codecool.shop.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.codecool.shop.Config;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Currency;

public class ProductDaoTest {

    private ProductDao productDao;
    private ProductCategory productCategory;
    private Supplier supplier;
    private Product product;
    private ProductCategoryDao productCategoryDao;
    private SupplierDao supplierDao;

    /**
     * Methods to test:
     * void add(ProductCategory category);
     * ProductCategory findByID(int id);
     * void removeOrder(int id);
     * List<ProductCategory> getAll();
     * List<Product> getBy(Supplier supplier);
     * List<Product> getBy(ProductCategory productCategory);
     */

    @BeforeEach
    void setUp() {
        Config.USE_PRODUCTION_DB = false;

        productDao = DaoFactory.getProductDao();
        productCategoryDao = DaoFactory.getProductCategoryDao();
        supplierDao = DaoFactory.getSupplierDao();

        productDao.removeAll();
        supplierDao.removeAll();
        productCategoryDao.removeAll();

        productCategory = new ProductCategory("Some name", "Some department", "Some description");
        int idOfProdCat = productCategoryDao.add(productCategory);
        productCategory.setId(idOfProdCat);

        supplier = new Supplier("Something", "Something");
        int idOfSupp = supplierDao.add(supplier);
        supplier.setId(idOfSupp);

        product = new Product("Product name", 5f, "USD", "masdassd", productCategory, supplier, "filename");
        int idOfProd = productDao.add(product);
        product.setId(idOfProd);
    }

    @Test
    void testFind() {
        int id = product.getId();
        System.out.println("testFind: product id - " + id);
        String name = product.getName();
        String desc = product.getDescription();
        float defaultPrice = product.getDefaultPrice();
        Currency currency = product.getDefaultCurrency();
        Supplier supplierOfProduct = product.getSupplier();
        ProductCategory productCategoryOfProduct = product.getProductCategory();
        String fileName = product.getImageFileName();

        System.out.println(id);
        Product foundProduct = productDao.find(id);
        System.out.println(foundProduct);

        assertAll("add-and-findByID",
                () -> {
                    assertNotNull(foundProduct);
                    assertAll("found-equals-original",
                            () -> assertEquals(name, foundProduct.getName()),
                            () -> assertEquals(desc, foundProduct.getDescription()),
                            () -> assertEquals(defaultPrice, foundProduct.getDefaultPrice()),
                            () -> assertEquals(currency, foundProduct.getDefaultCurrency()),
                            () -> assertEquals(supplierOfProduct.getId(), foundProduct.getSupplier().getId()),
                            () -> assertEquals(productCategoryOfProduct.getId(), foundProduct.getProductCategory().getId()),
                            () -> assertEquals(fileName, foundProduct.getImageFileName())
                    );
                }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        Product foundProduct = productDao.find(-1);
        assertNull(foundProduct);
    }

    @Test
    void testRemove() {
        int id = product.getId();
        Product foundProductBeforeRemove = productDao.find(id);
        productDao.remove(id);
        Product foundProductAfterRemove = productDao.find(id);

        assertAll("removeOrder",
                () -> assertNull(foundProductAfterRemove),
                () -> assertNotNull(foundProductBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(product.getId(), productDao.getAll().get(0).getId());
        assertEquals(1, productDao.getAll().size());
    }


    @Test
    void testGetBySupplier() {
        assertEquals(product.getId(), productDao.getBy(supplier).get(0).getId());
        assertEquals(1, productDao.getBy(supplier).size());
    }

    @Test
    void testGetByProductCategory() {
        assertEquals(product.getId(), productDao.getBy(productCategory).get(0).getId());
        assertEquals(1, productDao.getBy(productCategory).size());
    }

}