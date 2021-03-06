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
    public int add(Supplier supplier) {

        String query = "INSERT INTO suppliers (name, description) " +
                "       VALUES (?, ?) " +
                "       RETURNING id;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getDescription());

            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public Supplier find(int id) {

        String query = "SELECT name, description FROM suppliers WHERE id = ?;";

        try (DB db = new DB();
            PreparedStatement stmt = db.getPreparedStatement(query)
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

        String query = "DELETE FROM suppliers WHERE id = ?;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
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

        String query = "SELECT id, name, description FROM suppliers;";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
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

    @Override
    public void removeAll() {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("TRUNCATE TABLE suppliers CASCADE;")
        ){
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Suppliers deleted from database.");
            } else {
                System.out.println("Deletion failed from database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
