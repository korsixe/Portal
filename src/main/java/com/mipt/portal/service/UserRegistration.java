package com.mipt.portal.service;

public interface UserRegistration {
  User register(String email, String name, String password);

  boolean correctEmail(String email);

  boolean correctName(String name);

  boolean correctPassword(String password);

  boolean isEmailExists(String email);

  boolean isPasswordStrong(String password);
}