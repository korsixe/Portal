package com.mipt.portal.moderator;

import lombok.Data;

@Data
public class Moderator implements IModerator {

  private String username;
  private String email;
  private String password;
}