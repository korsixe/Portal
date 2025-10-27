package com.mipt.portal.announcement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class AdFilter implements IAdFilter {

  @Override
  public List<Announcement> filterByTitle(List<Announcement> ads, String title) { // в тупую сравниваниваем названия
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getTitle().equalsIgnoreCase(title)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Announcement> filterByCategory(List<Announcement> ads, Category category) {
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getCategory() == category) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Announcement> filterByPrice(List<Announcement> ads, int minPrice, int maxPrice) {
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getPrice() >= minPrice && ad.getPrice() <= maxPrice) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Announcement> filterByCondition(List<Announcement> ads, Condition condition) {
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getCondition() == condition) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений за последние N дней
  @Override
  public List<Announcement> filterByLastDays(List<Announcement> ads, int days) {
    Instant cutoffInstant = Instant.now().minusSeconds(days * 24 * 60 * 60L);
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getCreatedAt() != null && !ad.getCreatedAt().isBefore(cutoffInstant)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений новее определенной даты
  @Override
  public List<Announcement> filterByDateAfter(List<Announcement> ads, Instant date) {
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getCreatedAt() != null && !ad.getCreatedAt().isBefore(date)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений старше определенной даты
  @Override
  public List<Announcement> filterByDateBefore(List<Announcement> ads, Instant date) {
    List<Announcement> filteredAds = new ArrayList<>();
    for (Announcement ad : ads) {
      if (ad.getCreatedAt() != null && ad.getCreatedAt().isBefore(date)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  // Сортировка по популярности (по убыванию просмотров)
  @Override
  public List<Announcement> sortByPopularity(List<Announcement> ads) {
    List<Announcement> sortedAds = new ArrayList<>(ads);
    sortedAds.sort((ad1, ad2) -> Integer.compare(ad2.getViewCount(), ad1.getViewCount()));
    return sortedAds;
  }
}
