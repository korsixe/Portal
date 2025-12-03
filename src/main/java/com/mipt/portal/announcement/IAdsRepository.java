package com.mipt.portal.announcement;

import java.sql.SQLException;
import java.util.List;

public interface IAdsRepository {

  void createTables();

  void insertData();

  void insertDataComments();

  Long getUserIdByEmail(String email) throws SQLException;

  void updateAd(Announcement ad) throws SQLException;

  Announcement getAdById(long adId) throws SQLException;

  List<Long> getActiveAdIds() throws SQLException;

  long saveAd(Announcement ad) throws SQLException;

  List<Long> getModerAdIds() throws SQLException;

  boolean deleteAd(long adId) throws SQLException;

  boolean hardDeleteAd(long adId) throws SQLException;

  List<Long> getAllAdIds() throws SQLException;
}