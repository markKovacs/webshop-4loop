package com.codecool.shop.dao.implementation.memory;

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

    /*
    void add(ProductCategory category);
    ProductCategory find(int id);
    void remove(int id);
    List<ProductCategory> getAll();
    */

    @BeforeEach
    public void setUp() {
        dao = ProductCategoryDaoMem.getInstance();
        dao.getAll().clear();
        productCategory = new ProductCategory("Some name", "Some department", "Some description");
        supplier = new Supplier("Something", "Something");
        product = new Product("Product name", 5f, "USD", "masdassd", productCategory, supplier, null);
        dao.add(productCategory);
    }

    @Test
    void testAddAndFind() {
        int id = productCategory.getId();
        String name = productCategory.getName();
        String department = productCategory.getDepartment();
        String desc = productCategory.getDescription();
        List<Product> productList = productCategory.getProducts();

        ProductCategory foundCategory = dao.find(id);

        assertAll("add-and-find",
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
    void testRemove() {
        int id = productCategory.getId();
        ProductCategory foundProductCategoryBeforeRemove = dao.find(id);
        dao.remove(id);
        ProductCategory foundProductCategoryAfterRemove = dao.find(id);

        System.out.println("loasldsa");
        assertAll("remove",
                () -> assertNull(foundProductCategoryAfterRemove),
                () -> assertNotNull(foundProductCategoryBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        System.out.println(dao.getAll());
        System.out.println(productCategory);
        assertEquals(productCategory, dao.getAll().get(0));
        assertEquals(1, dao.getAll().size());
    }

}