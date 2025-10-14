package com.mipt.Portal.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
  private Map<String, String> users;

  public UserDatabase() {
    this.users = new HashMap<>();
  }

  public void addUser(String email, String password) {
    users.put(email, password);
  }

  public boolean containsUser(String email) {
    return users.containsKey(email);
  }

  public String getPassword(String email) {
    return users.get(email);
  }

}
