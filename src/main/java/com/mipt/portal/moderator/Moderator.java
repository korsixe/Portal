package com.mipt.portal.moderator;

import java.util.Objects;
import lombok.Data;

@Data
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