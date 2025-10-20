package com.mipt.portal.ad;

public interface IAd { // Интерфейс для получения данных объявления

  String getTitle();

  String getDescription();

  String getCategory();

  String getCondition();

  int getPrice();

  String getLocation();

  String getEmail();

  String getStatus();

  String toString();
}
