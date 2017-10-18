package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.util.ArrayList;
import java.util.List;

public class SupplierDaoMem implements SupplierDao {

    private static SupplierDaoMem instance = null;

    private List<Supplier> DATA = new ArrayList<>();
    private int supplierSequence = 0;

    private SupplierDaoMem() {
    }

    public static SupplierDaoMem getInstance() {
        if (instance == null) {
            instance = new SupplierDaoMem();
        }
        return instance;
    }

    @Override
    public int add(Supplier supplier) {
        supplier.setId(++supplierSequence);
        DATA.add(supplier);
        return supplier.getId();
    }

    @Override
    public Supplier find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        DATA.remove(find(id));
    }

    @Override
    public List<Supplier> getAll() {
        return DATA;
    }

    @Override
    public void removeAll() {
        getAll().clear();
    }
}
