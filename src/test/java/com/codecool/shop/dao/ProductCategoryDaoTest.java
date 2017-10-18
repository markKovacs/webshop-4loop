package com.codecool.shop.dao;

import com.codecool.shop.Config;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryDaoTest {

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
     */

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
        int id = productCategory.getId();
        String name = productCategory.getName();
        String department = productCategory.getDepartment();
        String desc = productCategory.getDescription();

        ProductCategory foundCategory = productCategoryDao.find(id);

        assertAll("add-and-findByID",
                () -> {
                    assertNotNull(foundCategory);
                    assertAll("found-equals-original",
                            () -> assertEquals(name, foundCategory.getName()),
                            () -> assertEquals(department, foundCategory.getDepartment()),
                            () -> assertEquals(desc, foundCategory.getDescription()),
                            () -> assertThrows(IndexOutOfBoundsException.class, () -> {
                                foundCategory.getProducts().get(1);
                            })
                    );
                }
        );
    }

    @Test
    void testNegativeNumberAsIndexReturnsNull() {
        ProductCategory foundCategory = productCategoryDao.find(-1);
        assertNull(foundCategory);
    }

    @Test
    void testRemove() {
        int id = productCategory.getId();
        ProductCategory foundProductCategoryBeforeRemove = productCategoryDao.find(id);
        productCategoryDao.remove(id);
        ProductCategory foundProductCategoryAfterRemove = productCategoryDao.find(id);

        assertAll("removeOrder",
                () -> assertNull(foundProductCategoryAfterRemove),
                () -> assertNotNull(foundProductCategoryBeforeRemove)
        );
    }

    @Test
    void testGetAll() {
        assertEquals(productCategory.getId(), productCategoryDao.getAll().get(0).getId());
        assertEquals(1, productCategoryDao.getAll().size());
    }

}