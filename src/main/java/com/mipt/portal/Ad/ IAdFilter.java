package main.java.com.mipt.portal.Ad;

import java.util.List;

public interface IAdFilter {

  List<Ad> filterByTitle(List<Ad> ads, String title);

  List<Ad> filterByCategory(List<Ad> ads, String category);

  List<Ad> filterByPrice(List<Ad> ads, int minPrice, int maxPrice);

  List<Ad> filterByCondition(List<Ad> ads, String condition);

  List<Ad> filterByNegotiablePrice(List<Ad> ads, boolean negotiable);
}
