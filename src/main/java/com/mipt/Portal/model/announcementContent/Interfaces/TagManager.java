package com.mipt.Portal.model.announcementContent.Interfaces;

import java.time.LocalDateTime;

public interface TagManager {


  User getManager();
  void setManager(User manager);

  LocalDateTime getActionTime();
  void setActionTime(LocalDateTime actionTime);

  void addAdditionalTag(String category, String value);
  void removeAdditionalTag(String category);

  void autoCompleteTags(String input);

  Boolean hasMaxTags();
  Boolean isValidTag();
  Boolean canAddMoreTags();
}