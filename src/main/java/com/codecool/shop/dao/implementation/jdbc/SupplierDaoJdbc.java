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
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("INSERT INTO suppliers (name, description) VALUES (?, ?);")
        ){
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getDescription());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("New supplier added to database.");
            } else {
                System.out.println("New supplier addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Supplier find(int id) {

        try (DB db = new DB();
            PreparedStatement stmt = db.getPreparedStatement("SELECT name, description FROM suppliers WHERE id = ?;")
        ){
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                Supplier supplier = new Supplier(resultSet.getString("name"),
                        resultSet.getString("description"));
                supplier.setId(id);
                return supplier;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void remove(int id) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("DELETE FROM suppliers WHERE id = ?;")
        ){
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Supplier deleted from database.");
            } else {
                System.out.println("Deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Supplier> getAll() {

        List resultList = new ArrayList();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("SELECT id, name, description FROM suppliers;")
        ){
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                Supplier supplier = new Supplier(
                        resultSet.getString("name"),
                        resultSet.getString("description"));
                supplier.setId(resultSet.getInt("id"));
                resultList.add(supplier);
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
