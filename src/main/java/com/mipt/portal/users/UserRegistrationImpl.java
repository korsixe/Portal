package com.mipt.portal.users;

import com.mipt.portal.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserRegistrationImpl implements UserRegistration {

    private final UserRepository userRepository;

    public UserRegistrationImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String email, String name, String password) {

        if (!correctEmail(email) || !correctName(name) || !correctPassword(password) || userRepository.existsByEmail(email) || !isPasswordStrong(password)) {
            if (userRepository.existsByEmail(email)) {
                System.out.println("Пользователь с email " + email  + " уже существует!");
            }
            return null;
        }
        System.out.println("Подтвердите пароль: ");
        Scanner scanner = new Scanner(System.in);
        String passwordAgain = scanner.nextLine();

        if (!passwordAgain.equals(password)) {
            System.out.println("Неверный пароль.");
            return null;
        }

        User newUser = new User(email, name, password);

        Optional<User> savedUser = userRepository.save(newUser);

        if(savedUser.isPresent()) {
            System.out.println("\nПользователь зарегистрирован!\n");
            return savedUser.get();
        } else {
            System.out.println("\nОшибка при регистрации пользователя.\n");
            return null ;
        }

    }

    @Override
    public boolean correctEmail(String email) {
        if (email == null || email.length() < 5) {
            System.out.println("Почта обязательна.");
            return false;
        }
        String emailPattern = "^[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]@phystech\\.edu$";

        if (!email.equals(email.toLowerCase())) {
            System.out.println("Почта должна быть в нижнем регистре!");
            return false;
        }

        if (!email.matches(emailPattern)) {
            System.out.println("Неправильный формат почты. Пример физтех-почты: ivanov.ii@phystech.edu");
            return false;
        }

        return true;
    }

    @Override
    public boolean correctName(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Ник обязателен!");
            return false;
        } else if (name.contains(" ")) {
            System.out.println("Ник должен быть без пробелов!");
            return false;
        }

        return true;
    }

    @Override
    public boolean correctPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 30) {
            if (password.length() > 30) {
                System.out.println("Длина пароля не более 30 символов!");
            } else {
                System.out.println("Длина пароля не менее 8 символов!");
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean isPasswordStrong(String password) {
        double hasLower = 0, hasUpper = 0,
                hasDigit = 0, hasSpecialChar = 0, goodSize = 0;
        List<String> strengthCriteria = List.of("!", "?", "@", "#", "$",
                "%", "%", "&", "*", "_", "-");
        for (char i : password.toCharArray()) {
            if (Character.isLowerCase(i)) {
                hasLower = 1;
            }
            if (Character.isUpperCase(i)) {
                hasUpper = 2;
            }
            if (Character.isDigit(i)) {
                hasDigit = 1.5;
            }
            if (strengthCriteria.contains(i)) {
                hasSpecialChar = 4;
            }
        }

        if (password.length() > 10) {
            goodSize = 1.5;
        }

        double strength = hasLower + hasUpper + hasDigit + hasSpecialChar + goodSize;

        System.out.println("Ваш пароль слишком простой!");
        if (strength >= 4) {
            return true;
        }
        return false;
    }
}