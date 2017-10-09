package com.codecool.shop;

import java.sql.*;

public class DB {

    private static final String DATABASE =
            "jdbc:" + Config.DB_TYPE + "://" + Config.HOST + ":" + Config.PORT + "/" + Config.DB_NAME;
    private static final String DB_USER = Config.USER;
    private static final String DB_PASSWORD = Config.PASSWORD;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DATABASE,
                DB_USER,
                DB_PASSWORD);
    }

    public void executeQuery(String query) {
        try (Connection connection = getConnection();
             Statement statement =connection.createStatement();
        ){
            statement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: generic queries could be implemented here

}
