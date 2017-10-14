package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.Config;
import com.codecool.shop.dao.DaoFactory;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierDaoTest {

    /**
     * Methods to test:
     * void add(Supplier supplier);
     * Supplier findByID(int id);
     * void removeOrder(int id);
     * List<Supplier> getAll();
     */

    // TODO - FURTHER TEST IDEAS
    // TODO - > adding the same ProductCategory multiple times should not be allowed

    private SupplierDao dao;
    private ProductCategory productCategory;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    public void setUp() {
        Config.USE_PRODUCTION_DB = false;
        dao = DaoFactory.getSupplierDao();
        dao.removeAll();
        productCategory = new ProductCategory("Some name", "Some department", "Some description");
        supplier = new Supplier("Something", "Something");
        product = new Product("Product name", 5f, "USD", "masdassd", productCategory, supplier, null);
        dao.add(supplier);
    }

    @Test
    void testFind() {
        int id = supplier.getId();
        String name = supplier.getName();
        String desc = supplier.getDescription();
        List<Product> productList = supplier.getProducts();

        Supplier foundSupplier = dao.find(id);

        assertAll("add-and-findByID",
                () -> {
                    assertNotNull(foundSupplier);
                    assertAll("found-equals-original",
                            () -> assertEquals(name, foundSupplier.getName()),
                            () -> assertEquals(desc, foundSupplier.getDescription()),
                            () -> assertEquals(productList.get(0), foundSupplier.getProducts().get(0)),
                            () -> assertThrows(IndexOutOfBoundsException.class, () -> {
                                foundSupplier.getProducts().get(1);
                            })
                    );
                }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        Supplier foundSupplier = dao.find(-1);
        assertNull(foundSupplier);
    }

    @Test
    void testRemove() {
        int id = supplier.getId();
        Supplier foundSupplierBeforeRemove = dao.find(id);
        dao.remove(id);
        Supplier foundSupplierAfterRemove = dao.find(id);

        assertAll("removeOrder",
                () -> assertNull(foundSupplierAfterRemove),
                () -> assertNotNull(foundSupplierBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(supplier, dao.getAll().get(0));
        assertEquals(1, dao.getAll().size());
    }

}