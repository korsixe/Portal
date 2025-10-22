package com.mipt.portal.ad;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ad {

  private long adId;                   // id объявления
  private String title;                // Заголовок
  private String description;          // Описание
  private Category category;           // Категория - используем enum
  private Condition condition;         // Состояние
  private int price;                   // Цена - если цена "-1" - договорная, если "0" - бесплатно, ">0" - цена
  private String location;             // Местоположение
  private long userId;                 // Храним id пользователя
  private String status;               // Активно, Архив, Черновик
  private Instant createdAt;           // дата создания объявления
  private Instant updatedAt;           // дата последнего обновления
  private int viewCount;               // счетчик просмотров
  private List<File> photoUrl;         // ссылки на фото (переделать в JSON массив???)

  // Конструктор
  public Ad(String title, String description, Category category, Condition condition,
      int price, String location, long userId, String status) {
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

  // Сеттеры
  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setCondition(Condition condition) {
    this.condition = condition;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setIdUser(long userId) {
    this.userId = userId;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public void setPhotoUrl(List<File> photoUrl) {
    this.photoUrl = photoUrl;
  }

  public void incrementViewCount() {
    this.viewCount++;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public Category getCategory() {
    return category;
  }

  public Condition getCondition() {
    return condition;
  }

  public int getPrice() {
    return price;
  }

  public String getLocation() {
    return location;
  }

  public long getUserId() {
    return userId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<File> getPhotoUrl() {
    return photoUrl;
  }

  public int getViewCount() {
    return viewCount;
  }

  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        .withZone(ZoneId.systemDefault()); // Указываем временную зону для форматирования Instant

    return "Заголовок: " + title +
        ", Описание: " + description +
        ", Категория: " + category.getDisplayName() +
        ", Состояние: " + condition.getDisplayName() +
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
