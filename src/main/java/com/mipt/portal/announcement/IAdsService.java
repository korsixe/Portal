package com.mipt.portal.announcement;

import java.sql.SQLException;

public interface IAdsService {

  Announcement createAd(long userId); //  Создать объявление

  Announcement editAd(Announcement ad) throws SQLException; // Изменить объявление

  Announcement deleteAd(long adId); // Удалить объявление

  Announcement getAd(long adId); // Получить объявление

  void sendToModeration(Announcement ad) throws SQLException;

  void activate(Announcement ad) throws SQLException;

  void archive(Announcement ad) throws SQLException;

  void saveAsDraft(Announcement ad) throws SQLException;

  void rejectModeration(Announcement ad) throws SQLException;

  void delete(Announcement ad) throws SQLException;
}