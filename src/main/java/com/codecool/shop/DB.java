package com.codecool.shop;

import java.sql.*;
import java.util.List;

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
            this.connection.setAutoCommit(false);
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            System.out.println("NOW WE SEND pstmt FROM getPreparedStatement!");
            return pstmt;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /*public int executeDML(PreparedStatement pstmt) {
        int affectedRows = -1;

        try {
            affectedRows = pstmt.executeUpdate();
            this.connection.commit();
        } catch (SQLException e) {
            try{
                this.connection.rollback();
            } catch (SQLException er) {
                er.printStackTrace();
            }
        } finally{
            try{
                if(pstmt!=null)
                    pstmt.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if(this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

        return affectedRows;
    }


    public ResultSet executeDQL(PreparedStatement pstmt) {

        try  {
            ResultSet resultSet = pstmt.executeQuery();
            //this.connection.commit();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }*/

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
