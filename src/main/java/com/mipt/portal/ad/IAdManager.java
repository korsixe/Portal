package com.mipt.portal.ad;

import java.util.List;

public interface IAdManager {

  Ad createAd(long userId); //  Создать объявление

  Ad editAd(Ad ad); // Изменить объявление

  Ad deleteAd(long adId); // Удалить объявление

  Ad getAd(long adId); // Получить объявление

  List<Ad> getAds(long userId); // Получить все объявления пользователя
}
