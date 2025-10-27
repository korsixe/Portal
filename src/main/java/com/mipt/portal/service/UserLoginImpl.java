package com.mipt.portal.service;

import com.mipt.portal.DatabaseManager;

import java.util.Optional;

public class UserLoginImpl implements UserLogin {
    private final DatabaseManager databaseManager;

    public UserLoginImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public User login(String email, String password) {
        if (!userExists(email)) {
            System.out.println("Пользователь с таким email не найден");
            return null;
        }

        Optional<User> user = databaseManager.getUserByEmail(email);

        if (passwordCorrect(password, user.get())) {
            System.out.println("Успешно! Добро пожаловать, " + user.get().getName() + "!");
            return user.get();
        }

        return null;
    }

    @Override
    public boolean userExists(String email) {
        return databaseManager.isEmailExists(email);
    }

    @Override
    public boolean passwordCorrect(String password, User user) {
        return user.getPassword().equals(password);
    }
}
