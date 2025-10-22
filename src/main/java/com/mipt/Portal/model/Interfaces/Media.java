package com.mipt.Portal.model.Interfaces;


import com.mipt.Portal.model.resources.Enums.MediaType;

import java.time.LocalDateTime;

public interface Media {
  Long getId();
  void setId(Long id);

  String getUrl();
  void setUrl(String url);

  MediaType getType();
  void setType(MediaType type);

  Integer getOrderIndex();
  void setOrderIndex(Integer orderIndex);

  Boolean getIsMain();
  void setIsMain(Boolean isMain);

  String getDescription();
  void setDescription(String description);

  Advertisement getAdvertisement();
  void setAdvertisement(Advertisement advertisement);

  LocalDateTime getUploadedAt();
  void setUploadedAt(LocalDateTime uploadedAt);

  Boolean isPhoto();
  Boolean isVideo();

  Boolean isValidDuration();
}