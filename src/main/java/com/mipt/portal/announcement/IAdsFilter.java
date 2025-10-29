package com.mipt.portal.announcement;

import java.time.Instant;
import java.util.List;

public interface IAdsFilter {

  List<Announcement> filterByTitle(List<Announcement> ads, String title);

  List<Announcement> filterByCategory(List<Announcement> ads, Category category);

  List<Announcement> filterByPrice(List<Announcement> ads, int minPrice, int maxPrice);

  List<Announcement> filterByCondition(List<Announcement> ads, Condition condition);

  List<Announcement> filterByLastDays(List<Announcement> ads, int days);

  List<Announcement> filterByDateAfter(List<Announcement> ads, Instant date);

  List<Announcement> filterByDateBefore(List<Announcement> ads, Instant date);

  List<Announcement> sortByPopularity(List<Announcement> ads);
}
