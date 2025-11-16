package com.mipt.portal.users;

public interface UserRegistration {

  User register(String email, String name, String password, String passwordAgain);
}