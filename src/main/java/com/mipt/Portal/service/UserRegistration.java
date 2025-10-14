package com.mipt.Portal.service;

public interface UserRegistration {
  User register(String email, String name, String password);

  boolean correctEmail(String email);

  boolean correctName(String name);

  boolean correctPassword(String password);

  boolean isEmailExists(String email);

  boolean isNameExists(String name);

  boolean isPasswordStrong(String password);
}
