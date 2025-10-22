package com.mipt.Portal.model.DTO;

import com.mipt.Portal.model.resources.Enums.AdvertisementStatus;
import com.mipt.Portal.model.resources.Enums.AdvertisementType;
import com.mipt.Portal.model.resources.Enums.Category;
import com.mipt.Portal.model.resources.Enums.ItemCondition;

import com.mipt.Portal.model.Interfaces.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Announcement {
  protected Long id;
  private String title;
  private String description;
  private Double price;
  private User owner; // хранит только id пользователя (?)
  private AdvertisementStatus status;


  private Category category;
  private String subcategory;
  private List tags = new ArrayList<>();

  private List<Media> media = new ArrayList<>();
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Integer viewCount = 0;
  private Integer tagsCount = 0;

  public Announcement(Long id, String title, String description, AdvertisementType type,
                      Double price, User owner, Category category, ItemCondition condition) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.price = price;
    this.owner = owner;
    this.category = category;
  }

  public Announcement() {
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Double getPrice() { return price; }
  public void setPrice(Double price) { this.price = price; }

  public User getOwner() { return owner; }
  public void setOwner(User owner) { this.owner = owner; }

  public Category getCategory() { return category; }
  public void setCategory(Category category) { this.category = category; }

  public List<Media> getMedia() { return media; }
  public void setMedia(List<Media> media) { this.media = media; }

  public List getTags() { return tags; }
  public void setTags(List tags) { this.tags = tags; }

  public AdvertisementStatus getStatus() { return status; }
  public void setStatus(AdvertisementStatus status) { this.status = status; }

  public Integer getViewCount() { return viewCount; }
  public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public String getSubcategory() {return subcategory;}
  public void setSubcategory(String subcategory) {this.subcategory = subcategory;}
}
