package com.mipt.Portal.model;

import java.time.LocalDateTime;

public interface Tag {
  Long getId();
  void setId(Long id);

  String getName();
  void setName(String name);

  TagType getType();
  void setType(TagType type);

  String getCategory();
  void setCategory(String category);

  Integer getUsageCount();
  void setUsageCount(Integer usageCount);

  LocalDateTime getCreatedAt();
  void setCreatedAt(LocalDateTime createdAt);
  void incrementUsageCount();

  Boolean isPredefined();
  Boolean isCustom();
  Boolean isValidForCategory(String category);
}