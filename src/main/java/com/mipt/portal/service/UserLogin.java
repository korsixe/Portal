package com.mipt.portal.service;

public interface UserLogin {
  User login(String email, String password);

  boolean userExists(String email);

  boolean passwordCorrect(String password, User user);
}