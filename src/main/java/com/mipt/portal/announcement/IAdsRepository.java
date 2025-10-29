package com.mipt.portal.announcement;

import java.sql.SQLException;

public interface IAdsRepository {
  void createTables() throws SQLException;
  void insertData() throws SQLException;
  Long getUserIdByEmail(String email) throws SQLException;
  void updateAd(Announcement ad) throws SQLException;
  Announcement getAdById(long adId) throws SQLException;
  long saveAd(Announcement ad) throws SQLException;
  boolean deleteAd(long adId) throws SQLException;
  boolean hardDeleteAd(long adId) throws SQLException;
}
