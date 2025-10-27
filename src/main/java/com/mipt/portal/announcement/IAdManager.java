package com.mipt.portal.announcement;

import java.util.List;

public interface IAdManager {

  Announcement createAd(long userId); //  Создать объявление

  Announcement editAd(Announcement ad); // Изменить объявление

  Announcement deleteAd(long adId); // Удалить объявление

  Announcement getAd(long adId); // Получить объявление

  List<Announcement> getAds(long userId); // Получить все объявления пользователя
}
