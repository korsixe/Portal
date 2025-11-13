package com.mipt.portal.announcement;

import com.mipt.portal.users.service.UserService;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data

public class AdsService implements IAdsService {

  private AdsRepository adsRepository;
  private UserService userService;

  public AdsService() {
    try {
      this.adsRepository = new AdsRepository();
      this.userService = new UserService();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Announcement createAd(long userId, String title, String description, Category category,
      String subcategory, Condition condition, int price, String location, List<File> photos,
      List<String> tags, AdvertisementStatus action) throws SQLException {

    Announcement ad = new Announcement(title, description, category, condition, price, location, userId);
    ad.setSubcategory(subcategory);
    ad.setPhotos(photos);
    ad.setTags(tags);
    ad.setStatus(action);
    ad.setId(adsRepository.saveAd(ad));
    userService.addAnnouncementId(ad.getUserId(), ad.getId());
    return ad;
  }

  @Override
  public Long getUserIdByEmail(String email) throws SQLException {
    return adsRepository.getUserIdByEmail(email);
  }

  @Override
  public Announcement editAd(Announcement ad) throws SQLException {
    ad.setUpdatedAt(Instant.now());
    adsRepository.updateAd(ad);
    System.out.println("✅ Изменения сохранены успешно!");
    return ad;
  }

  @Override
  public Announcement deleteAd(long adId) {
    try {
      Announcement ad = adsRepository.getAdById(adId);
      if (ad == null) {
        System.out.println("❌ Объявление с ID " + adId + " не найдено");
        return null;
      }

      // Подтверждение удаления
      Scanner scanner = new Scanner(System.in);
      System.out.println("Вы действительно хотите удалить объявление?");
      System.out.println(ad.toString());
      System.out.print("Введите 'да' для подтверждения: ");
      String confirmation = scanner.nextLine();

      if ("да".equalsIgnoreCase(confirmation)) {
        boolean deleted = adsRepository.deleteAd(adId);
        if (deleted) {
          System.out.println("✅ Объявление успешно удалено");
          return ad;
        } else {
          System.out.println("❌ Не удалось удалить объявление");
          return null;
        }
      } else {
        System.out.println("❌ Удаление отменено");
        return null;
      }

    } catch (SQLException e) {
      System.err.println("❌ Ошибка при удалении объявления: " + e.getMessage());
      return null;
    }
  }

  @Override
  public Announcement getAd(long adId) {
    try {
      Announcement ad = adsRepository.getAdById(adId);
      if (ad != null) {
        System.out.println("✅ Объявление найдено:");
        System.out.println(ad.toString());
        ad.incrementViewCount(); // увеличиваем просмотры
      } else {
        System.out.println("❌ Объявление с ID " + adId + " не найдено");
      }
      return ad;
    } catch (SQLException e) {
      System.err.println("❌ Ошибка при получении объявления: " + e.getMessage());
      return null;
    }
  }

  @Override
  public void sendToModeration(Announcement ad) throws SQLException {
    ad.sendToModeration();
    adsRepository.updateAd(ad);
  }

  @Override
  public void activate(Announcement ad) throws SQLException {
    ad.activate();
    adsRepository.updateAd(ad);
  }

  @Override
  public void archive(Announcement ad) throws SQLException {
    ad.archive();
    adsRepository.updateAd(ad);
  }

  @Override
  public void saveAsDraft(Announcement ad) throws SQLException {
    ad.saveAsDraft();
    adsRepository.updateAd(ad);
  }

  @Override
  public void rejectModeration(Announcement ad) throws SQLException {
    ad.rejectModeration();
    adsRepository.updateAd(ad);
  }

  @Override
  public void delete(Announcement ad) throws SQLException {
    ad.delete();
    adsRepository.updateAd(ad);
  }

  public List<Long> getModerAdIds() throws SQLException {
    return adsRepository.getModerAdIds();
  }
}