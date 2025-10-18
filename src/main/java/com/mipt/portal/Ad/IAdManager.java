package main.java.com.mipt.portal.Ad;

public interface IAdManager {

  Ad createAd(int idUser); //  Создать объявление

  Ad editAd(Ad ad); // Изменить объявление

  void deleteAd(int adId); // Удалить объявление

  Ad getAd(int adId); // Получить объявление
}
