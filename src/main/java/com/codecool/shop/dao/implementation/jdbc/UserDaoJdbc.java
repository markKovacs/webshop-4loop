package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.user.User;

import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {

    @Override
    public List<String> update(int userId, Map<String, String> profileInput) {
        User user = find(userId);
        List<String> errorMessages = user.setProfileInformation(profileInput);
        if (errorMessages.size() == 0) {
            // TODO: do UPDATE database
        }
        return errorMessages;
    }

}
