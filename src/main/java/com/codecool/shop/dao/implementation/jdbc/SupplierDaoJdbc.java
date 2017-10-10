package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoJdbc implements SupplierDao {

    @Override
    public void add(Supplier supplier) {

    }

    @Override
    public Supplier find(int id) {

        try (DB db = new DB();
            PreparedStatement stmt = db.getPreparedStatement("SELECT ?, ? FROM suppliers WHERE id = ?;")
        ){
            stmt.setString(1, "name");
            stmt.setString(2, "description");
            stmt.setInt(3, id);
            ResultSet resultSet = stmt.executeQuery();
            System.out.println(resultSet.getFetchSize());
            if(resultSet.next()){
                Supplier supplier = new Supplier(resultSet.getString("name"),
                        resultSet.getString("description"));
                System.out.println(supplier.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public List<Supplier> getAll() {

        List resultList = new ArrayList();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("SELECT id, name, description FROM suppliers;")
        ){
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                resultList.add(new Supplier(
                        resultSet.getString("name"),
                        resultSet.getString("description")));
            }
            System.out.println(resultList);
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
