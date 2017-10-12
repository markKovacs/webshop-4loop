package com.codecool.shop.dao.implementation.memory;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDaoMem implements UserDao {

    private List<User> DATA = new ArrayList<>();

    @Override
    public List<String> update(int userId, Map<String, String> profileInput) {
        List<String> errorMessages = new ArrayList<>();

        User user = DATA.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
        if (user == null) {
            errorMessages.add("Could not find user profile.");
            return errorMessages;
        }

        errorMessages = user.setProfileInformation(profileInput);
        return errorMessages;
    }

    @Override
    public void add(User user) {
        user.setUserId(DATA.size() + 1);
        DATA.add(user);
    }

    @Override
    public User find(int userId) {
        return DATA.stream().filter(user -> user.getUserId() == userId).findFirst().orElse(null);
    }

    @Override
    public User find(String email) {
        return DATA.stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
    }

    @Override
    public void remove(int id) {
        DATA.remove(find(id));
    }

    @Override
    public List<String> getAllEmails() {
        return DATA.stream().map(User::getEmail).collect(Collectors.toList());
    }
}
