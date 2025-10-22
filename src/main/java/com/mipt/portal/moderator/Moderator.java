package com.mipt.portal.moderator;

import java.util.Objects;

public class Moderator implements IModerator {

  private String username;
  private String email;
  private String password;

  // Конструкторы
  public Moderator() {
  }

  public Moderator(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  // Getters and Setters

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  // equals и hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Moderator moderator = (Moderator) o;
    return Objects.equals(username, moderator.username) &&
        Objects.equals(email, moderator.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email);
  }

  @Override
  public String toString() {
    return "Moderator{" +
        "username='" + username + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}