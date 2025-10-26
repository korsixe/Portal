package com.mipt.portal;

import com.mipt.portal.announcement.Announcement;
import java.sql.SQLException;

public interface IDatabaseManager {
  void createTables() throws SQLException;
  void insertData() throws SQLException;
  Long getUserIdByEmail(String email) throws SQLException;
  void updateAd(Announcement ad) throws SQLException;
  Announcement getAdById(long adId) throws SQLException;
  long saveAd(Announcement ad) throws SQLException;
  boolean deleteAd(long adId) throws SQLException;

}