package com.mipt.Portal.model;


import java.time.LocalDateTime;
import java.util.List;

public interface AdvertisementContent {
  Long getId();
  void setId(Long id);

  String getTitle();
  void setTitle(String title);

  String getDescription();
  void setDescription(String description);

  AdvertisementType getType();
  void setType(AdvertisementType type);

  Double getPrice();
  void setPrice(Double price);

  PricePeriod getPricePeriod();
  void setPricePeriod(PricePeriod pricePeriod);

  User getOwner();
  void setOwner(User owner);

  Category getCategory();
  void setCategory(Category category);

  Location getLocation();
  void setLocation(Location location);

  List<Media> getMedia();
  void setMedia(List<Media> media);

  List<Tag> getTags();
  void setTags(List<Tag> tags);

  AdvertisementStatus getStatus();
  void setStatus(AdvertisementStatus status);

  Integer getViewCount();
  void setViewCount(Integer viewCount);

  LocalDateTime getCreatedAt();
  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();
  void setUpdatedAt(LocalDateTime updatedAt);

  void addMedia(Media media);
  void removeMedia(Media media);

  void addTag(Tag tag);
  void removeTag(Tag tag);

  void ViewsCount();

  Boolean isValidContent();
  Boolean hasMaxMedia();
  Boolean hasMaxTags();
}
