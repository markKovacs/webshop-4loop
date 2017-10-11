package com.codecool.shop.dao;

import com.codecool.shop.user.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    void add(User order);
    User find(int id);
    User find(String email);
    void remove(int id);
    List<String> update(int userId, Map<String, String> profileInput);
    List<String> getAllEmails();

}
