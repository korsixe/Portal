package com.mipt.portal.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Ad implements IAd {

  private String title;                // Заголовок
  private String description;          // Описание
  private Category category;           // Категория - используем enum
  private String condition;            // Состояние
  private int price;                   // Цена - если цена "-1" - договорная, если "0" - бесплатно, ">0" - цена
  private String location;             // Местоположение
  private String email;               // Храним почту
  private String status;               // Активно, Архив, Черновик

  // Конструктор
  public Ad(String title, String description, Category category, String condition, int price,
      String location, String email, String status) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.price = price;
    this.location = location;
    this.email = email;
    this.status = status;
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

  public void setCondition(String condition) {
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
  public String getCondition() {
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
  public String toString() {
    return "Заголовок: " + title +
        ", Описание: " + description +
        ", Категория: " + category.getDisplayName() +
        ", Состояние: " + condition +
        ", Цена: " + formatPrice() +
        ", Местоположение: " + location +
        ", Создатель: " + email +
        ", Состояние: " + status;
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
