package com.mipt.portal.users;

import com.mipt.portal.users.repository.UserRepository;
import com.mipt.portal.users.util.UserValidator;

import java.util.Optional;

public class UserRegistrationImpl implements UserRegistration {

  private final UserRepository userRepository;

  public UserRegistrationImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User register(String email, String name, String password, String passwordAgain) {
    try {

      UserValidator.validateEmail(email);
      UserValidator.validateName(name);
      UserValidator.validatePassword(password);
      UserValidator.isPasswordStrong(password);

      if (userRepository.existsByEmail(email)) {
        System.out.println("Пользователь с email " + email + " уже существует!");
        return null;
      }

      if (!passwordAgain.equals(password)) {
        System.out.println("Неверный пароль.");
        return null;
      }

      User newUser = new User(email, name, password);
      Optional<User> savedUser = userRepository.save(newUser);

      if (savedUser.isPresent()) {
        System.out.println("\nПользователь зарегистрирован!\n");
        return savedUser.get();
      } else {
        System.out.println("\nОшибка при регистрации пользователя.\n");
        return null;
      }

    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}