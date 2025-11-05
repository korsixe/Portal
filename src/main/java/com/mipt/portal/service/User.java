package com.mipt.portal.service;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class User {
  private String email;
  private String name;
  private long id;
  private String password;
  private String address;
  private String studyProgram;
  private int course;
  private double rating;
  private int coins;
  private ArrayList<Long> adList;

  public User() {}

  public User(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.course = 1;
    this.adList = adList;
    //this.id = DataBase.createId(email);
  }

  public void increaseRating(double increment) {
    if (this.rating + increment <= 5) {
      this.rating += increment;
    } else {
      this.rating = 5;
    }
  }

  public void decreaseRating(double decrement) {
    if (this.rating - decrement >= 1) {
      this.rating -= decrement;
    } else {
      this.rating = 1;
    }
  }


  public void addCoins(int amount) {
    this.coins += amount;
  }

  public boolean spendCoins(int amount) {
    if (this.coins - amount < 0) {
      return false;
    }
    this.coins -= amount;
    return true;
  }

  @Override
  public String toString() {
    return String.format("Имя: " + name + " \n" +
            "Email: " + email + "\n" +
            "Адрес: " + address + "\n" +
            "Программа обучени, курс: " + studyProgram + " " +  course + " курс\n" +
            "Рейтинг: " + rating + "\n" +
            "Физтех-коины: " + coins);
  }
}
