package com.mipt.portal.ad;

public interface IAdManager {

  Ad createAd(String email); //  Создать объявление

  Ad editAd(Ad ad); // Изменить объявление

  void deleteAd(int adId); // Удалить объявление

  Ad getAd(int adId); // Получить объявление
}
