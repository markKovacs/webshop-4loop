package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.DB;
import com.codecool.shop.dao.UserDao;
import com.codecool.shop.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {

    @Override
    public List<String> update(int userId, Map<String, String> profileInput) {
        List<String> errorMessages = new ArrayList<>();

        User user = find(userId);
        if (user == null) {
            errorMessages.add("Could not find user profile.");
            return errorMessages;
        }

        errorMessages = user.setProfileInformation(profileInput);
        if (errorMessages.size() == 0) {
            String query = "UPDATE users SET phone_number = ?, " +
                    "billing_country = ?, billing_city = ?, billing_zip = ?, billing_address = ?, " +
                    "shipping_country = ?, shipping_city = ?, shipping_zip = ?, shipping_address = ? " +
                    "WHERE id = ?;";
            try (DB db = new DB();
                 PreparedStatement stmt = db.getPreparedStatement(query)
            ) {
                stmt.setString(1, profileInput.get("phone"));
                stmt.setString(2, profileInput.get("billcountry"));
                stmt.setString(3, profileInput.get("billcity"));
                stmt.setString(4, profileInput.get("billzip"));
                stmt.setString(5, profileInput.get("billaddress"));
                stmt.setString(6, profileInput.get("shipcountry"));
                stmt.setString(7, profileInput.get("shipcity"));
                stmt.setString(8, profileInput.get("shipzip"));
                stmt.setString(9, profileInput.get("shipaddress"));
                stmt.setInt(10, userId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0){
                    System.out.println("User updated in database.");
                } else {
                    System.out.println("User update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return errorMessages;
    }

    @Override
    public void add(User user) {
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(
                     "INSERT INTO users (name, email, password) " +
                             "VALUES (?, ?, ?);")
        ){
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("New user added to database.");
            } else {
                System.out.println("New user addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User find(int id) {
        String query = "SELECT id, name, email, password, phone_number, " +
                "billing_country, billing_city, billing_zip, billing_address, " +
                "shipping_country, shipping_city, shipping_zip, shipping_address " +
                "WHERE id = ?;";
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ){
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                User user = User.create(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("billing_country"),
                        resultSet.getString("billing_city"),
                        resultSet.getString("billing_zip"),
                        resultSet.getString("billing_address"),
                        resultSet.getString("shipping_country"),
                        resultSet.getString("shipping_city"),
                        resultSet.getString("shipping_zip"),
                        resultSet.getString("shipping_address")
                );
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User find(String email) {
        String query = "SELECT id, name, email, password, phone_number, " +
                "billing_country, billing_city, billing_zip, billing_address, " +
                "shipping_country, shipping_city, shipping_zip, shipping_address " +
                "WHERE email = ?;";
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ){
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                User user = User.create(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("billing_country"),
                        resultSet.getString("billing_city"),
                        resultSet.getString("billing_zip"),
                        resultSet.getString("billing_address"),
                        resultSet.getString("shipping_country"),
                        resultSet.getString("shipping_city"),
                        resultSet.getString("shipping_zip"),
                        resultSet.getString("shipping_address")
                );
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void remove(int id) {
        String query = "DELETE FROM users WHERE id = ?;";
        try (DB db = new DB();
            PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                System.out.println("User deleted from database.");
            } else {
                System.out.println("User deletion failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAllEmails() {
        List<String> emails = new ArrayList<>();
        String query = "SELECT email FROM users;";
        try (DB db = new DB();
             PreparedStatement stmt = db.getPreparedStatement(query)
        ) {
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()) {
                emails.add(resultSet.getString("email"));
            }
            return emails;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
