package com.mipt.portal.ad;

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
}
