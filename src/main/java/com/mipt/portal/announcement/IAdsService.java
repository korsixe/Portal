package com.mipt.portal.announcement;

import java.sql.SQLException;

public interface IAdsService {

  Announcement createAd(long userId); //  Создать объявление

  Announcement editAd(Announcement ad) throws SQLException; // Изменить объявление

  Announcement deleteAd(long adId); // Удалить объявление

  Announcement getAd(long adId); // Получить объявление
}
