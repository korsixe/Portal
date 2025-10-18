package com.mipt.portal.Ad;

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
  public List<Ad> filterByCategory(List<Ad> ads, String category) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCategory().equalsIgnoreCase(category)) {
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
  public List<Ad> filterByCondition(List<Ad> ads, String condition) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.getCondition().equalsIgnoreCase(condition)) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }

  @Override
  public List<Ad> filterByNegotiablePrice(List<Ad> ads, boolean negotiable) {
    List<Ad> filteredAds = new ArrayList<>();
    for (Ad ad : ads) {
      if (ad.isNegotiablePrice() == negotiable) {
        filteredAds.add(ad);
      }
    }
    return filteredAds;
  }
}
