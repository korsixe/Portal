package com.mipt.portal.announcement;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class AdsFilter implements IAdsFilter {

  AdsRepository adsRepository;

  public AdsFilter(AdsRepository adsRepository) {
    this.adsRepository = adsRepository;
  }

  @Override
  public List<Long> filterByTitle(List<Long> ads, String title)
      throws SQLException { // в тупую сравниваниваем названия
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getTitle().equalsIgnoreCase(title)) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Long> filterByCategory(List<Long> ads, Category category) throws SQLException {
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getCategory() == category) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Long> filterByPrice(List<Long> ads, int minPrice, int maxPrice) throws SQLException {
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getPrice() >= minPrice
          && adsRepository.getAdById(idAd).getPrice() <= maxPrice) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Long> filterByCondition(List<Long> ads, Condition condition) throws SQLException {
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getCondition() == condition) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений за последние N дней
  @Override
  public List<Long> filterByLastDays(List<Long> ads, int days) throws SQLException {
    Instant cutoffInstant = Instant.now().minusSeconds(days * 24 * 60 * 60L);
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getCreatedAt() != null && !adsRepository.getAdById(idAd)
          .getCreatedAt().isBefore(cutoffInstant)) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений новее определенной даты
  @Override
  public List<Long> filterByDateAfter(List<Long> ads, Instant date) throws SQLException {
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getCreatedAt() != null && !adsRepository.getAdById(idAd)
          .getCreatedAt().isBefore(date)) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  // Фильтрация объявлений старше определенной даты
  @Override
  public List<Long> filterByDateBefore(List<Long> ads, Instant date) throws SQLException {
    List<Long> filteredAds = new ArrayList<>();
    for (Long idAd : ads) {
      if (adsRepository.getAdById(idAd).getCreatedAt() != null && adsRepository.getAdById(idAd)
          .getCreatedAt().isBefore(date)) {
        filteredAds.add(idAd);
      }
    }
    return filteredAds;
  }

  // Сортировка по популярности (по убыванию просмотров)
  @Override
  public List<Long> sortByPopularity(List<Long> ads) {
    List<Long> sortedAds = new ArrayList<>(ads);
    sortedAds.sort((ad1, ad2) -> {
      try {
        return Integer.compare(adsRepository.getAdById(ad1).getViewCount(),
            adsRepository.getAdById(ad2).getViewCount());
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
    return sortedAds;
  }
}