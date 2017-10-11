package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDaoMem implements UserDao {

    @Override
    public List<String> update(int userId, Map<String, String> profileInput) {





        return null;
    }

    private List<User> DATA = new ArrayList<>();

    @Override
    public void add(User user) {

    }

    @Override
    public User find(int id) {
        return null;
    }

    @Override
    public User find(String email) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public List<String> getAllEmails() {
        return null;
    }
}
