package com.mipt.portal.announcementContent.interfaces;


import java.time.LocalDateTime;

public interface MediaManager {
  Media getMedia();
  void setMedia(Media media);

  User getManager();
  void setManager(User manager);

  LocalDateTime getActionTime();
  void setActionTime(LocalDateTime actionTime);

  void uploadPhoto();
  void deletePhoto();

  void uploadVideo();
  void deleteVideo();

  void setAsMain();

  void addDescription(String description);
  Boolean isValidPhoto();
  Boolean isValidVideo();
  Boolean canManageMedia();
}