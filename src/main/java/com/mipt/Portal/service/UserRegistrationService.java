package com.mipt.Portal.service;

public class UserRegistrationService implements UserRegistration {


  @Override
  public User register(String email, String name, String password) {
    if (!correctData(email, name, password)) {
      return null;
    }

    User newUser = new UserClass(email.trim(), name.trim(), password.trim());
    System.out.println(newUser);
    return newUser;
  }

  private boolean correctData(String email, String name, String password) {
    if (!correctEmail(email) || !correctName(name) || !correctPassword(password)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean correctEmail(String email) {
    if (isEmailExists(email)) {
      System.out.println("Email занят!");
      return false;
    }
    if (email == null || email.trim().isEmpty() || !email.contains("@")) {
      System.out.println("Email обязателен!");
      return false;
    }
    return true;
  }

  @Override
  public boolean correctName(String name) {
    if (isNameExists(name)) {
      System.out.println("Имя занято!");
      return false;
    }
    if (name == null || name.trim().isEmpty()) {
      System.out.println("Имя обязателеьно!");
      return false;
    }
    return true;
  }

  @Override
  public boolean correctPassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      System.out.println("Пароль обязателен!");
      return false;
    }
    return isPasswordStrong(password);
  }

  @Override
  public boolean isEmailExists(String email) {
    return false;
  }

  @Override
  public boolean isNameExists(String name) {
    return false;
  }

  @Override
  public boolean isPasswordStrong(String password) {
    if (password.length() < 8) {
      System.out.println("Пароль должен быть не менее 8 символов!");
      return false;
    }
    return true;
  }
}
