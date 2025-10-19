package com.mipt.portal.Ad;

public interface IAd { // Интерфейс для получения данных объявления

  String getTitle();

  String getDescription();

  String getCategory();

  String getCondition();

  boolean isNegotiablePrice();

  int getPrice();

  String getLocation();

  String getIdUser();

  String getStatus();

  String toString();
}
