package com.mipt.portal.ad;

import java.sql.SQLException;

public interface IAdManager {

  Ad createAd(long userId); //  Создать объявление

  Ad editAd(Ad ad) throws SQLException; // Изменить объявление

  Ad deleteAd(long adId); // Удалить объявление

  Ad getAd(long adId); // Получить объявление
}
