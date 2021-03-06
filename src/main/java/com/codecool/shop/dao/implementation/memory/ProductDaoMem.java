package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDaoMem implements ProductDao {

    private static ProductDaoMem instance = null;

    private List<Product> DATA = new ArrayList<>();
    private int productSequence = 0;

    private ProductDaoMem() {
    }

    public static ProductDaoMem getInstance() {
        if (instance == null) {
            instance = new ProductDaoMem();
        }
        return instance;
    }

    @Override
    public int add(Product product) {
        product.setId(++productSequence);
        DATA.add(product);
        return product.getId();
    }

    @Override
    public Product find(int id) {
        return DATA.stream().filter(product -> product.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        DATA.remove(find(id));
    }

    @Override
    public List<Product> getAll() {
        return DATA;
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        return DATA.stream().filter(product -> product.getSupplier().equals(supplier)).collect(Collectors.toList());
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        return DATA.stream().filter(product -> product.getProductCategory().equals(productCategory)).collect(Collectors.toList());
    }

    @Override
    public void removeAll() {
        getAll().clear();
    }
}
