package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDaoJdbc implements ProductCategoryDao {

    @Override
    public void add(ProductCategory category) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("INSERT INTO categories (name, department, description) VALUES (?, ?, ?);")
        ){
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDepartment());
            stmt.setString(3, category.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("New category added to database.");
            } else {
                System.out.println("New category addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductCategory find(int id) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("SELECT name, department, description FROM categories WHERE id = ?;")
        ){
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                ProductCategory category = new ProductCategory(
                        resultSet.getString("name"),
                        resultSet.getString("department"),
                        resultSet.getString("description"));
                category.setId(id);
                return category;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void remove(int id) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("DELETE FROM categories WHERE id = ?;")
        ){
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Category deleted from database.");
            } else {
                System.out.println("Deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ProductCategory> getAll() {

        List resultList = new ArrayList();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("SELECT id, name, department, description FROM categories;")
        ){
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                ProductCategory productCategory = new ProductCategory(
                        resultSet.getString("name"),
                        resultSet.getString("department"),
                        resultSet.getString("description"));
                productCategory.setId(resultSet.getInt("id"));
                resultList.add(productCategory);
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
