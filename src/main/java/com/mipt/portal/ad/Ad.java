package com.mipt.portal.ad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ad implements IAd {

  private String title;                // Заголовок
  private String description;          // Описание
  private Category category;           // Категория - используем enum
  private Condition condition;         // Состояние
  private int price;                   // Цена - если цена "-1" - договорная, если "0" - бесплатно, ">0" - цена
  private String location;             // Местоположение
  private String email;                // Храним почту
  private String status;               // Активно, Архив, Черновик
  private LocalDate createdAt;         // дата создания объявления
  private LocalDate updatedAt;         // дата последнего обновления
  private int viewCount;               // счетчик просмотров
  private List<String> photoUrl;       // ссылки на фото (переделать в JSON массив???)

  // Конструктор
  public Ad(String title, String description, Category category, Condition condition,
      int price, String location, String email, String status) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.price = price;
    this.location = location;
    this.email = email;
    this.status = status;
    this.createdAt = LocalDate.now();
    this.updatedAt = LocalDate.now();
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

  public void setIdUser(String email) {
    this.email = email;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public void setPhotoUrl(List<String> photoUrl) {
    this.photoUrl = photoUrl;
  }

  @Override
  public void incrementViewCount() {
    this.viewCount++;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Category getCategory() {
    return category;
  }

  @Override
  public Condition getCondition() {
    return condition;
  }

  @Override
  public int getPrice() {
    return price;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public LocalDate getCreatedAt() {
    return createdAt;
  }

  @Override
  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public List<String> getPhotoUrl() {
    return photoUrl;
  }

  @Override
  public int getViewCount() {
    return viewCount;
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    return "Заголовок: " + title +
        ", Описание: " + description +
        ", Категория: " + category.getDisplayName() +
        ", Состояние: " + condition.getDisplayName() +
        ", Цена: " + formatPrice() +
        ", Местоположение: " + location +
        ", Создатель: " + email +
        ", Статус: " + status +
        ", Просмотров: " + viewCount +
        ", Создано: " + (createdAt != null ? createdAt.format(formatter) : "не указано") +
        ", Обновлено: " + (updatedAt != null ? updatedAt.format(formatter) : "не указано");
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
