package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.memory.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoJdbcTest {

    private ProductDao dao;
    private ProductCategory productCategory;
    private Supplier supplier;
    private Product product;

    /**
     * Methods to test:
     * void add(ProductCategory category);
     * ProductCategory find(int id);
     * void remove(int id);
     * List<ProductCategory> getAll();
     * List<Product> getBy(Supplier supplier);
     * List<Product> getBy(ProductCategory productCategory);
     */

    // TODO - FURTHER TEST IDEAS
    // TODO - > adding the same ProductCategory multiple times should not be allowed
    // TODO - > adding Product to DAO that is null
    // TODO - > creating more products and test getBy in cases it should not find products

    @BeforeEach
    public void setUp() {
        dao = new ProductDaoJdbc();
        dao.getAll().clear();
        productCategory = new ProductCategory("Some name", "Some department", "Some description");
        supplier = new Supplier("Something", "Something");
        product = new Product("Product name", 5f, "USD", "masdassd", productCategory, supplier, "filename");
        dao.add(product);
    }

    @Test
    void testFind() {
        int id = product.getId();
        String name = product.getName();
        String desc = product.getDescription();
        float defaultPrice = product.getDefaultPrice();
        Currency currency = product.getDefaultCurrency();
        Supplier supplierOfProduct = product.getSupplier();
        ProductCategory productCategoryOfProduct = product.getProductCategory();
        String fileName = product.getImageFileName();

        Product foundProduct = dao.find(id);

        assertAll("add-and-find",
                () -> {
                    assertNotNull(foundProduct);
                    assertAll("found-equals-original",
                            () -> assertEquals(name, foundProduct.getName()),
                            () -> assertEquals(desc, foundProduct.getDescription()),
                            () -> assertEquals(defaultPrice, foundProduct.getDefaultPrice()),
                            () -> assertEquals(currency, foundProduct.getDefaultCurrency()),
                            () -> assertEquals(supplierOfProduct, foundProduct.getSupplier()),
                            () -> assertEquals(productCategoryOfProduct, foundProduct.getProductCategory()),
                            () -> assertEquals(fileName, foundProduct.getImageFileName())
                    );
                }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        Product foundProduct = dao.find(-1);
        assertNull(foundProduct);
    }

    @Test
    void testRemove() {
        int id = product.getId();
        Product foundProductBeforeRemove = dao.find(id);
        dao.remove(id);
        Product foundProductAfterRemove = dao.find(id);

        assertAll("remove",
                () -> assertNull(foundProductAfterRemove),
                () -> assertNotNull(foundProductBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(product, dao.getAll().get(0));
        assertEquals(1, dao.getAll().size());
    }


    @Test
    void testGetBySupplier() {
        assertEquals(product, dao.getBy(supplier).get(0));
        assertEquals(1, dao.getBy(supplier).size());
    }

    @Test
    void testGetByProductCategory() {
        assertEquals(product, dao.getBy(productCategory).get(0));
        assertEquals(1, dao.getBy(productCategory).size());
    }

}