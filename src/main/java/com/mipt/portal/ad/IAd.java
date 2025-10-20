package com.mipt.portal.ad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface IAd { // Интерфейс для получения данных объявления

  String getTitle();

  String getDescription();

  Category getCategory();

  Condition getCondition();

  int getPrice();

  String getLocation();

  String getEmail();

  String getStatus();

  String toString();

  LocalDate getCreatedAt();

  LocalDate getUpdatedAt();
}
