package com.codecool.shop;

import java.sql.*;

public class DB implements AutoCloseable {

    private static String DATABASE =
            "jdbc:" + Config.DB_TYPE + "://" + Config.HOST + ":" + Config.PORT + "/" + Config.DB_NAME;
    private static String DB_USER = Config.USER;
    private static String DB_PASSWORD = Config.PASSWORD;
    private Connection connection;

    public DB() {
        if (Config.USE_PRODUCTION_DB) {
            DATABASE =
                    "jdbc:" + Config.DB_TYPE + "://" + Config.HOST + ":" + Config.PORT + "/" + Config.DB_NAME;
            DB_USER = Config.USER;
            DB_PASSWORD = Config.PASSWORD;
        } else {
            DATABASE =
                    "jdbc:" + Config.TEST_DB_TYPE + "://" + Config.TEST_HOST + ":" + Config.TEST_PORT + "/" + Config.TEST_DB_NAME;
            DB_USER = Config.TEST_USER;
            DB_PASSWORD = Config.TEST_PASSWORD;
        }
    }

    private Connection getConnection() {
        try{
            return DriverManager.getConnection(
                    DATABASE,
                    DB_USER,
                    DB_PASSWORD);
        }catch (SQLException em){
            em.printStackTrace();
        }

        return null;
    }

    public PreparedStatement getPreparedStatement(String query) {
        try {
            this.connection = getConnection();
            this.connection.setAutoCommit(true);
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            return pstmt;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws SQLException {
        try {
            if(this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}
