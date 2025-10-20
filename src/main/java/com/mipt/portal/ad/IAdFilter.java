package com.mipt.portal.ad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

public interface IAdFilter {

  List<Ad> filterByTitle(List<Ad> ads, String title);

  List<Ad> filterByCategory(List<Ad> ads, Category category);

  List<Ad> filterByPrice(List<Ad> ads, int minPrice, int maxPrice);

  List<Ad> filterByCondition(List<Ad> ads, Condition condition);

  List<Ad> filterByLastDays(List<Ad> ads, int days);

  List<Ad> filterByDateAfter(List<Ad> ads, LocalDate date);

  List<Ad> filterByDateBefore(List<Ad> ads, LocalDate date);
}
