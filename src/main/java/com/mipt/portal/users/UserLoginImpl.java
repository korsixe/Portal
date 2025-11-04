package com.mipt.portal.users;

import com.mipt.portal.UserRepository;

import java.util.Optional;

public class UserLoginImpl implements UserLogin {
    private final UserRepository userRepository;

    public UserLoginImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptinal = userRepository.findByEmail(email);

        if (userOptinal.isEmpty()) {
            System.out.println("Пользователь с таким email не найден");
            return null;
        }

        User user = userOptinal.get();

        if (passwordCorrect(password, user)) {
            System.out.println("Успешно! Добро пожаловать, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("Неверный пароль.");
            return null;
        }
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean passwordCorrect(String password, User user) {
        return user.getPassword().equals(password);
    }
}
