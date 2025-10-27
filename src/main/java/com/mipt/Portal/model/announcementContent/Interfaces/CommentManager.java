package com.mipt.Portal.model.announcementContent.Interfaces;

import java.time.LocalDateTime;

public interface CommentManager {
  Comment getComment();
  void setComment(Comment comment);

  User getManager();
  void setManager(User manager);

  LocalDateTime getActionTime();
  void setActionTime(LocalDateTime actionTime);

  void addComment(Comment comment);
  void editComment(String newText);
  void deleteComment();
  void replyToComment(Comment reply);

  void likeComment();
  void unlikeComment();

  void addMediaToComment(Media media);
  void removeMediaFromComment(Media media);

  Boolean canEditComment();
  Boolean canDeleteComment();
}