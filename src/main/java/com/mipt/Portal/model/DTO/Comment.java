package com.mipt.Portal.model.DTO;

import java.time.LocalDateTime;

public class Comment {
  private Long id;
  private String author;
  private String text;
  private LocalDateTime createdAt;
  private Long advertisementId;
  private String advertisementTitle;


  public Comment(Long id, String author, String text, LocalDateTime createdAt,
                    Long parentCommentId, Comment parentComment) {
    this.id = id;
    this.author = author;
    this.text = text;
    this.createdAt = createdAt;
    this.advertisementId = advertisementId;
    this.advertisementTitle = advertisementTitle;
  }

  public Long getId() {return id;}
  public void setId(Long id) {this.id = id;}

  public String getAuthor() {return author;}
  public void setAuthor(String author) {this.author = author;}

  public String getText() {return text;}
  public void setText(String text) {this.text = text;}

  public LocalDateTime getCreatedAt() {return createdAt;}
  public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

  public void setAdvertisementId(Long advertisementId) {this.advertisementId = advertisementId;}
  public String getAdvertisementTitle() {return advertisementTitle;}
}
