package com.mipt.Portal.service;

public class UserLoginService implements UserLogin {
  private UserDatabase userDatabase;

  public UserLoginService() {
    this.userDatabase = new UserDatabase();
  }

  @Override
  public User login(String email, String password) {

    if (!userDatabase.containsUser(email)) {
      System.out.println("Пользователя с email " + email + " не существует!");
      return null;
    }

    String storedPassword = userDatabase.getPassword(email);

    if (!storedPassword.equals(password)) {
      System.out.println("Неверный пароль!");
      return null;
    }

    System.out.println("Успешный вход!");
    return null;
  }
}
