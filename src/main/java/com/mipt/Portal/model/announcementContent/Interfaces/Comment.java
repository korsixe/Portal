package com.mipt.Portal.model.announcementContent.Interfaces;

import java.time.LocalDateTime;
import java.util.List;

public interface Comment {
  Long getId();
  void setId(Long id);

  String getText();
  void setText(String text);

  User getAuthor();
  void setAuthor(User author);

  Advertisement getAdvertisement();
  void setAdvertisement(Advertisement advertisement);

  Comment getParentComment();
  void setParentComment(Comment parentComment);

  List<Comment> getReplies();
  void setReplies(List<Comment> replies);

  List<Media> getAttachedMedia();
  void setAttachedMedia(List<Media> attachedMedia);

  Integer getLikes();
  void setLikes(Integer likes);

  LocalDateTime getCreatedAt();
  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();
  void setUpdatedAt(LocalDateTime updatedAt);

  void addReply(Comment reply);
  void removeReply(Comment reply);

  void addMedia(Media media);

  void like();
  void unlike();

  Boolean isRootComment();
  Boolean canBeEditedBy(User user);
}