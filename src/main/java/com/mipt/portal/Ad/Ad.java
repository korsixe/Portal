package main.java.com.mipt.portal.Ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Ad implements IAd {

  private String title;                // Заголовок
  private String description;          // Описание
  private String category;             // Категория - когда подвяжем БД, будет через int
  private String condition;            // Состояние
  private boolean negotiablePrice;     // Цена договорная
  private int price;                   // Цена
  private String location;             // Местоположение
  private int idUser;                  // Пока храним id user, чтобы знать как связываться
  private String status;               // Активно, Архив, Черновик

  // Конструктор
  public Ad(String title, String description, String category, String condition,
      boolean negotiablePrice,
      int price, String location, int idUser, String status) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.condition = condition;
    this.negotiablePrice = negotiablePrice;
    this.price = price;
    this.location = location;
    this.idUser = idUser;
    this.status = status;
  }

  // Сеттеры
  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public void setNegotiablePrice(boolean negotiablePrice) {
    this.negotiablePrice = negotiablePrice;
  }

  public void setPrice(int price) {
    if (price >= 0) {
      this.price = price;
    } else {
      throw new IllegalArgumentException("Цена не может быть отрицательной.");
    }
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setIdUser(int idUser) {
    this.idUser = idUser;
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
  public String getCategory() {
    return category;
  }

  @Override
  public String getCondition() {
    return condition;
  }

  @Override
  public boolean isNegotiablePrice() {
    return negotiablePrice;
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
  public int getIdUser() {
    return idUser;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "Заголовок: " + title +
        ", Описание: " + description +
        ", Категория: " + category +
        ", Состояние: " + condition +
        ", Договорная цена? " + negotiablePrice +
        ", Цена: " + price + " руб." +
        ", Местоположение: " + location +
        ", Создатель " + idUser +
        ", Состояние " + status;
  }
}
