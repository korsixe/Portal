package com.mipt.portal.service;

public class User {
  private String email;
  private String name;
  private String password;
  private String address;
  private String sturyProgram;
  private int course;
  private double rating;
  private int coins;

  public User() {}

  public User(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.course = 1;
  }

  public static void main(String[] args) {

  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getStudyProgram() {
    return sturyProgram;
  }

  public void setStudyProgram(String studyProgram) {
    this.sturyProgram = studyProgram;
  }

  public int getCourse() {
    return course;
  }

  public void setCourse(int course) {
    this.course = course;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rating) {
    this.rating = rating;
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

  public int getCoins() {
    return coins;
  }

  public void setCoins(int coins) {
    this.coins = coins;
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
    return String.format("\uD83D\uDC64 Имя: " + name + " \n" +
            "\uD83D\uDCE7 Email: " + email + "\n" +
            "\uD83D\uDCE7 Адрес: " + address + "\n" +
            "\uD83C\uDF93 Программа обучени, курс: " + sturyProgram + " " +  course + " курс\n" +
            "\u2B50 Рейтинг: " + rating + "\n" +
            "\uD83E\uDE99 Физтех-коины: " + coins);
  }
}