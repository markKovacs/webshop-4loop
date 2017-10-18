package com.codecool.shop.dao;

import com.codecool.shop.Config;
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

    private ProductDao productDao;
    private ProductCategory productCategory;
    private Supplier supplier;
    private Product product;
    private ProductCategoryDao productCategoryDao;
    private SupplierDao supplierDao;

    @BeforeEach
    public void setUp() {
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
        int id = supplier.getId();
        String name = supplier.getName();
        String desc = supplier.getDescription();
        List<Product> productList = supplier.getProducts();

        Supplier foundSupplier = supplierDao.find(id);

        assertAll("add-and-findByID",
                () -> {
                    assertNotNull(foundSupplier);
                    assertAll("found-equals-original",
                            () -> assertEquals(name, foundSupplier.getName()),
                            () -> assertEquals(desc, foundSupplier.getDescription()),
                            () -> assertThrows(IndexOutOfBoundsException.class, () -> {
                                foundSupplier.getProducts().get(1);
                            })
                    );
                }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        Supplier foundSupplier = supplierDao.find(-1);
        assertNull(foundSupplier);
    }

    @Test
    void testRemove() {
        int id = supplier.getId();
        Supplier foundSupplierBeforeRemove = supplierDao.find(id);
        supplierDao.remove(id);
        Supplier foundSupplierAfterRemove = supplierDao.find(id);

        assertAll("removeOrder",
                () -> assertNull(foundSupplierAfterRemove),
                () -> assertNotNull(foundSupplierBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(supplier.getId(), supplierDao.getAll().get(0).getId());
        assertEquals(1, supplierDao.getAll().size());
    }

}