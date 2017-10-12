package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.Config;
import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryDaoMemTest {

    private ProductCategoryDao dao;
    private ProductCategory productCategory;
    private Supplier supplier;
    private Product product;

    /**
     * Methods to test:
     * void add(ProductCategory category);
     * ProductCategory findByID(int id);
     * void remove(int id);
     * List<ProductCategory> getAll();
     */

    // TODO - FURTHER TEST IDEAS
    // TODO - > adding the same ProductCategory multiple times should not be allowed

    @BeforeEach
    public void setUp() {
        Config.USE_PRODUCTION_DB = false;
        dao = DaoFactory.getProductCategoryDao();
        dao.clearAll();
        productCategory = new ProductCategory("Some name", "Some department", "Some description");
        supplier = new Supplier("Something", "Something");
        product = new Product("Product name", 5f, "USD", "masdassd", productCategory, supplier, null);
        dao.add(productCategory);
    }

    @Test
    void testFind() {
        int id = productCategory.getId();
        String name = productCategory.getName();
        String department = productCategory.getDepartment();
        String desc = productCategory.getDescription();
        List<Product> productList = productCategory.getProducts();

        ProductCategory foundCategory = dao.find(id);

        assertAll("add-and-findByID",
            () -> {
                assertNotNull(foundCategory);
                assertAll("found-equals-original",
                        () -> assertEquals(name, foundCategory.getName()),
                        () -> assertEquals(department, foundCategory.getDepartment()),
                        () -> assertEquals(desc, foundCategory.getDescription()),
                        () -> assertEquals(productList.get(0), foundCategory.getProducts().get(0)),
                        () -> assertThrows(IndexOutOfBoundsException.class, () -> {
                            foundCategory.getProducts().get(1);
                        })
                );
            }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        ProductCategory foundCategory = dao.find(-1);
        assertNull(foundCategory);
    }

    @Test
    void testRemove() {
        int id = productCategory.getId();
        ProductCategory foundProductCategoryBeforeRemove = dao.find(id);
        dao.remove(id);
        ProductCategory foundProductCategoryAfterRemove = dao.find(id);

        assertAll("remove",
                () -> assertNull(foundProductCategoryAfterRemove),
                () -> assertNotNull(foundProductCategoryBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(productCategory, dao.getAll().get(0));
        assertEquals(1, dao.getAll().size());
    }

}