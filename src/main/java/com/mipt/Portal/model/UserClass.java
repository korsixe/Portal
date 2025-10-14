package com.mipt.Portal.model;

public class UserClass implements User {

  private String email;
  private String name;
  private String password;
  private String address;
  private String studyProgram;
  private Integer course = 1;
  private Double rating = 0.0;
  private Integer coins = 1000;

  public UserClass(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
  }


  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getAdress() {
    return address;
  }

  @Override
  public void setAdress(String adress) {
    this.address = adress;
  }

  @Override
  public String getStudyProgram() {
    return studyProgram;
  }

  @Override
  public void setStudyProgram(String studyProgram) {
    this.studyProgram = studyProgram;
  }

  @Override
  public int getCourse() {
    return course;
  }

  @Override
  public void setCourse(int course) {
    this.course = course;
  }

  @Override
  public double getRating() {
    return rating;
  }

  @Override
  public void setRating(double rating) {
    this.rating = rating;
  }

  @Override
  public void increaseRating(double increment) {
    this.rating += increment;
  }

  @Override
  public void decreaseRating(double decrement) {
    this.rating -= decrement;
  }

  @Override
  public int getCoins() {
    return coins;
  }

  @Override
  public void setCoins(int coins) {
    this.coins = coins;
  }

  @Override
  public boolean addCoins(int amount) {
    if (amount > 0) {
      this.coins += amount;
      return true;
    };
    return false;
  }

  @Override
  public boolean spendCoins(int amount) {
    if (amount > 0 && this.coins >= amount) {
      this.coins -= amount;
      return true;
    }
    return false;
  }

  @Override
  public boolean hasEnoughCoins(int amount) {
    return coins >= amount;
  }
}
