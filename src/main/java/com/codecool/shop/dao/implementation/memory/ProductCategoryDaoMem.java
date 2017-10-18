package com.codecool.shop.dao.implementation.memory;


import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDaoMem implements ProductCategoryDao {

    private static ProductCategoryDaoMem instance = null;

    private List<ProductCategory> DATA = new ArrayList<>();
    private int categorySequence = 0;

    private ProductCategoryDaoMem() {
    }

    public static ProductCategoryDaoMem getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoMem();
        }
        return instance;
    }

    @Override
    public int add(ProductCategory category) {
        category.setId(++categorySequence);
        DATA.add(category);
        return category.getId();
    }

    @Override
    public ProductCategory find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        DATA.remove(find(id));
    }

    @Override
    public List<ProductCategory> getAll() {
        return DATA;
    }

    @Override
    public void removeAll() {
        getAll().clear();
    }
}
