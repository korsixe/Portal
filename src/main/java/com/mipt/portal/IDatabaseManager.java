package com.mipt.portal;

import com.mipt.portal.ad.Ad;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDatabaseManager {
  void createTables() throws SQLException;
  void insertData() throws SQLException;
  Long getUserIdByEmail(String email) throws SQLException;
  void updateAd(Ad ad) throws SQLException;
  Ad getAdById(long adId) throws SQLException;
  long saveAd(Ad ad) throws SQLException;
  boolean deleteAd(long adId) throws SQLException;

}