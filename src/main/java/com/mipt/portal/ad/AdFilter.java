package com.mipt.portal.ad;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;


public class AdFilter implements IAdFilter {

  @Override
  public List<Ad> filterByTitle(List<Ad> ads, String title) { // в тупую сравниваниваем названия
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getTitle().equalsIgnoreCase(title)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Ad> filterByCategory(List<Ad> ads, Category category) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCategory() == category) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Ad> filterByPrice(List<Ad> ads, int minPrice, int maxPrice) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getPrice() >= minPrice && ad.getPrice() <= maxPrice) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Ad> filterByCondition(List<Ad> ads, Condition condition) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCondition() == condition) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений за последние N дней
  @Override
  public List<Ad> filterByLastDays(List<Ad> ads, int days) {
    Instant cutoffInstant = Instant.now().minusSeconds(days * 24 * 60 * 60L);
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCreatedAt() != null && !ad.getCreatedAt().isBefore(cutoffInstant)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений новее определенной даты
  @Override
  public List<Ad> filterByDateAfter(List<Ad> ads, Instant date) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCreatedAt() != null && !ad.getCreatedAt().isBefore(date)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений старше определенной даты
  @Override
  public List<Ad> filterByDateBefore(List<Ad> ads, Instant date) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCreatedAt() != null && ad.getCreatedAt().isBefore(date)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Сортировка по популярности (по убыванию просмотров)
  @Override
  public List<Ad> sortByPopularity(List<Ad> ads) {
    List<Ad> sortedAds = new ArrayList<>(ads);
    sortedAds.sort((ad1, ad2) -> Integer.compare(ad2.getViewCount(), ad1.getViewCount()));
    return sortedAds;
  }
}
