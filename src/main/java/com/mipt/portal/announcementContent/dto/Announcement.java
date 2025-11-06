package com.mipt.portal.announcementContent.dto;

import com.mipt.portal.announcementContent.announcementResources.Enums.AdvertisementStatus;
import com.mipt.portal.announcementContent.announcementResources.Enums.Category;
import com.mipt.portal.announcementContent.announcementResources.Enums.Condition;


import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data

public class Announcement {

  private long id;                     // id объявления
  private String title;                // Заголовок
  private String description;          // Описание
  private Category category;           // Категория - используем enum
  private String subcategory;
  private Condition condition;
  private int price;                   // Цена - если цена "-1" - договорная, если "0" - бесплатно, ">0" - цена
  private String location;             // Местоположение
  private long userId;                 // Храним id пользователя
  private AdvertisementStatus status;
  private Instant createdAt;           // дата создания объявления
  private Instant updatedAt;           // дата последнего обновления
  private Integer viewCount;           // счетчик просмотров
  private List<File> photos;          // ссылки на фото
  private List<String> tags;
  private Integer tagsCount;


  // Конструкторы

  public Announcement(Announcement other) {
    this.id = other.id;
    this.title = other.title;
    this.description = other.description;
    this.category = other.category;
    this.condition = other.condition;
    this.price = other.price;
    this.location = other.location;
    this.userId = other.userId;
    this.status = other.status;
    this.createdAt = other.createdAt;
    this.updatedAt = other.updatedAt;
    this.viewCount = other.viewCount;
    this.photos = other.photos != null ? new ArrayList<>(other.photos) : null;
  }


  public Announcement(String title, String description, Category category, Condition condition,
                      int price, String location, long userId, AdvertisementStatus status) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.price = price;
    this.location = location;
    this.userId = userId;
    this.status = status;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.viewCount = 0;
  }

  public Announcement() {

  }

  public void incrementViewCount() {
    this.viewCount++;
  }

  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
      .withZone(ZoneId.systemDefault());

    return "Заголовок: " + title +
      ", Описание: " + description +
      ", Категория: " + category +
      ", Состояние: " + condition +
      ", Цена: " + formatPrice() +
      ", Местоположение: " + location +
      ", Создатель: " + userId +
      ", Статус: " + status +
      ", Просмотров: " + viewCount +
      ", Создано: " + (createdAt != null ? formatter.format(createdAt) : "не указано") +
      ", Обновлено: " + (updatedAt != null ? formatter.format(updatedAt) : "не указано");
  }

  private String formatPrice() {
    if (price == -1) {
      return "договорная";
    } else if (price == 0) {
      return "бесплатно";
    } else {
      return price + " руб.";
    }
  }
}