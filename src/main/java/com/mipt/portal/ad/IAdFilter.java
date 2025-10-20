package com.mipt.portal.ad;

import java.util.List;

public interface IAdFilter {

  List<Ad> filterByTitle(List<Ad> ads, String title);

  List<Ad> filterByCategory(List<Ad> ads, Category category);

  List<Ad> filterByPrice(List<Ad> ads, int minPrice, int maxPrice);

  List<Ad> filterByCondition(List<Ad> ads, Condition condition);
}
