package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoJdbc implements ProductDao {

    @Override
    public void add(Product product) {

        String query = "INSERT INTO products (name, description, category_id," +
                        "supplier_id, price, currency, image_filename) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ){
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setInt(3, product.getProductCategory().getId());
            stmt.setInt(4, product.getSupplier().getId());
            stmt.setFloat(5, product.getDefaultPrice());
            stmt.setString(6, product.getDefaultCurrency().toString());
            stmt.setString(7, product.getImageFileName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("New product added to database.");
            } else {
                System.out.println("New product addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product find(int id) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(
                     "SELECT p.id product_id, p.name product_name, " +
                             "p.description product_description, " +
                             "p.category_id category_id, c.name category_name, " +
                             "c.department category_department, " +
                             "c.description category_description, p.supplier_id supplier_id, " +
                             "s.name supplier_name, s.description supplier_description, " +
                             "p.price product_price, p.currency product_currency, " +
                             "p.image_filename " +
                             "FROM products p " +
                             "JOIN categories c ON c.id = p.category_id " +
                             "JOIN suppliers s ON s.id = p.supplier_id " +
                             "WHERE p.id = ?;")
        ){
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                ProductCategory category = new ProductCategory(
                        resultSet.getString("category_name"),
                        resultSet.getString("category_department"),
                        resultSet.getString("category_description"));
                category.setId(resultSet.getInt("category_id"));
                Supplier supplier = new Supplier(
                        resultSet.getString("supplier_name"),
                        resultSet.getString("supplier_description"));
                supplier.setId(resultSet.getInt("supplier_id"));
                Product product = new Product(
                        resultSet.getString("product_name"),
                        resultSet.getFloat("product_price"),
                        resultSet.getString("product_currency"),
                        resultSet.getString("product_description"),
                        category,
                        supplier,
                        resultSet.getString("image_filename")
                );
                product.setId(resultSet.getInt("product_id"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void remove(int id) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement("DELETE FROM products WHERE id = ?;")
        ){
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Product deleted from database.");
            } else {
                System.out.println("Deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAll() {
        List<Product> resultList = new ArrayList<>();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(
                     "SELECT p.id product_id, p.name product_name, p.description product_description, " +
                             "p.category_id category_id, c.name category_name, " +
                             "c.department category_department, " +
                             "c.description category_description, p.supplier_id supplier_id, " +
                             "s.name supplier_name, s.description supplier_description, " +
                             "p.price product_price, p.currency product_currency, p.image_filename " +
                             "FROM products p " +
                             "JOIN categories c ON c.id = p.category_id " +
                             "JOIN suppliers s ON s.id = p.supplier_id")
        ){
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                ProductCategory category = new ProductCategory(
                        resultSet.getString("category_name"),
                        resultSet.getString("category_department"),
                        resultSet.getString("category_description")
                );
                category.setId(resultSet.getInt("category_id"));
                Supplier supplier = new Supplier(
                        resultSet.getString("supplier_name"),
                        resultSet.getString("supplier_description"));
                supplier.setId(resultSet.getInt("supplier_id"));
                Product product = new Product(
                        resultSet.getString("product_name"),
                        resultSet.getFloat("product_price"),
                        resultSet.getString("product_currency"),
                        resultSet.getString("product_description"),
                        category,
                        supplier,
                        resultSet.getString("image_filename")
                );
                product.setId(resultSet.getInt("product_id"));
                resultList.add(product);
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public List<Product> getBy(Supplier selectedSupplier) {
        List<Product> resultList = new ArrayList();
        int selectedSupplierId = selectedSupplier.getId();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(
                     "SELECT p.id product_id, p.name product_name, p.description product_description, " +
                             "p.category_id category_id, c.name category_name, " +
                             "c.department category_department, " +
                             "c.description category_description, p.supplier_id supplier_id, " +
                             "s.name supplier_name, s.description supplier_description, " +
                             "p.price product_price, p.currency product_currency, " +
                             "p.image_filename " +
                             "FROM products p " +
                             "JOIN categories c ON c.id = p.category_id " +
                             "JOIN suppliers s ON s.id = p.supplier_id " +
                             "WHERE s.id = ?;")
        ){
            stmt.setInt(1, selectedSupplierId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                ProductCategory category = new ProductCategory(
                        resultSet.getString("category_name"),
                        resultSet.getString("category_department"),
                        resultSet.getString("category_description")
                );
                category.setId(resultSet.getInt("category_id"));
                Supplier supplier = new Supplier(
                        resultSet.getString("supplier_name"),
                        resultSet.getString("supplier_description"));
                supplier.setId(resultSet.getInt("supplier_id"));
                Product product = new Product(
                        resultSet.getString("product_name"),
                        resultSet.getFloat("product_price"),
                        resultSet.getString("product_currency"),
                        resultSet.getString("product_description"),
                        category,
                        supplier,
                        resultSet.getString("image_filename")
                );
                product.setId(resultSet.getInt("product_id"));
                resultList.add(product);
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        List<Product> resultList = new ArrayList();
        int selectedCategoryId = productCategory.getId();

        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(
                     "SELECT p.id product_id, p.name product_name, p.description product_description, " +
                             "p.category_id category_id, c.name category_name, " +
                             "c.department category_department, " +
                             "c.description category_description, p.supplier_id supplier_id, " +
                             "s.name supplier_name, s.description supplier_description, " +
                             "p.price product_price, p.currency product_currency, " +
                             "p.image_filename " +
                             "FROM products p " +
                             "JOIN categories c ON c.id = p.category_id " +
                             "JOIN suppliers s ON s.id = p.supplier_id " +
                             "WHERE c.id = ?;")
        ){
            stmt.setInt(1, selectedCategoryId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                ProductCategory category = new ProductCategory(
                        resultSet.getString("category_name"),
                        resultSet.getString("category_department"),
                        resultSet.getString("category_description")
                );
                category.setId(resultSet.getInt("category_id"));
                Supplier supplier = new Supplier(
                        resultSet.getString("supplier_name"),
                        resultSet.getString("supplier_description"));
                supplier.setId(resultSet.getInt("supplier_id"));
                Product product = new Product(
                        resultSet.getString("product_name"),
                        resultSet.getFloat("product_price"),
                        resultSet.getString("product_currency"),
                        resultSet.getString("product_description"),
                        category,
                        supplier,
                        resultSet.getString("image_filename")
                );
                product.setId(resultSet.getInt("product_id"));
                resultList.add(product);
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
             PreparedStatement stmt = db.getPreparedStatement("DELETE FROM products;")
        ){
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("Products deleted.");
            } else {
                System.out.println("Deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
