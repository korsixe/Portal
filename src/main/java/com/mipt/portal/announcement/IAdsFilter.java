package com.mipt.portal.announcement;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public interface IAdsFilter {

  List<Long> filterByTitle(List<Long> ads, String title) throws SQLException;

  List<Long> filterByCategory(List<Long> ads, Category category) throws SQLException;

  List<Long> filterByPrice(List<Long> ads, int minPrice, int maxPrice) throws SQLException;

  List<Long> filterByCondition(List<Long> ads, Condition condition) throws SQLException;

  List<Long> filterByLastDays(List<Long> ads, int days) throws SQLException;

  List<Long> filterByDateAfter(List<Long> ads, Instant date) throws SQLException;

  List<Long> filterByDateBefore(List<Long> ads, Instant date) throws SQLException;

  List<Long> sortByPopularity(List<Long> ads);
}