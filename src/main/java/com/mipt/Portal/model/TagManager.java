package com.mipt.Portal.model;

import java.time.LocalDateTime;

public interface TagManager {
  Tag getTag();
  void setTag(Tag tag);

  User getManager();
  void setManager(User manager);

  LocalDateTime getActionTime();
  void setActionTime(LocalDateTime actionTime);

  void addTag(Tag tag);
  void removeTag(Tag tag);

  void selectTag(Tag predefinedTag);

  void addAdditionalTag(String category, String value);
  void removeAdditionalTag(String category);

  void autoCompleteTags(String input);

  Boolean hasMaxTags();
  Boolean isValidTag();
  Boolean canAddMoreTags();
}