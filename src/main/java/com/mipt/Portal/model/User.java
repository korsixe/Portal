package com.mipt.Portal.model;

public interface User {

  String getEmail();

  void setEmail(String email);

  String getName();

  void setName(String name);

  String getPassword();

  void setPassword(String password);

  String getAdress();

  void setAdress(String adress);

  String getStudyProgram();

  void setStudyProgram(String studyProgram);

  int getCourse();

  void setCourse(int course);

  double getRating();

  void setRating(double rating);

  void increaseRating(double increment);

  void decreaseRating(double decrement);


  /** если будем делать коины */

  int getCoins();

  void setCoins(int coins);

  boolean addCoins(int amount);

  boolean spendCoins(int amount);

  boolean hasEnoughCoins(int amount);

}

