package com.mipt.portal.moderator;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Moderator {

  private Long id;
  private String email;
  private String name;
  private String password;
  private List<String> permissions;

  public Moderator() {
    this.permissions = new ArrayList<>();
  }

  public Moderator(String email, String name, String password) {
    this();
    this.email = email;
    this.name = name;
    this.password = password;
  }

  public boolean hasPermission(String permission) {
    return permissions.contains(permission) || permissions.contains("ALL");
  }

  @Override
  public String toString() {
    return String.format("Модератор: %s (%s)", name, email);
  }
}