package com.mipt.portal.announcement;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public interface IAdsService {

  Announcement createAd(long userId, String title, String description, Category category,
      String subcategory, Condition condition, int price, String location, List<File> photos,
      List<String> tags, AdvertisementStatus action) throws SQLException;

  Long getUserIdByEmail(String email) throws SQLException;

  Announcement editAd(Announcement ad) throws SQLException; // Изменить объявление

  Announcement deleteAd(long adId); // Удалить объявление

  Announcement hardDeleteAd(long adId);

  Announcement getAd(long adId); // Получить объявление

  List<Long> searchAdsByString(List<Long> adsId, String query) throws SQLException;

  void sendToModeration(Announcement ad) throws SQLException;

  void activate(Announcement ad) throws SQLException;

  void archive(Announcement ad) throws SQLException;

  void saveAsDraft(Announcement ad) throws SQLException;

  void rejectModeration(Announcement ad) throws SQLException;

  void delete(Announcement ad) throws SQLException;
}