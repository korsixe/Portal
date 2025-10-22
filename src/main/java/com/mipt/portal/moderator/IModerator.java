package com.mipt.portal.moderator;

import java.util.Objects;

public interface IModerator {

  String getUsername();

  String getEmail();

  String getPassword();

  boolean equals(Object o);
}