package com.mipt.portal.announcement;

import com.mipt.portal.users.service.UserService;
import com.mipt.portal.database.DatabaseConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    Announcement ad = new Announcement(title, description, category, condition, price, location,
        userId);
    ad.setSubcategory(subcategory);
    ad.setPhotos(photos);
    ad.setTags(tags);
    ad.setTagsCount(tags != null ? tags.size() : 0);
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

    // Обновляем объявление вместе с фото и тегами в одном запросе
    adsRepository.updateAd(ad);

    System.out.println("✅ Изменения сохранены успешно! ID: " + ad.getId());
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

      boolean deleted = adsRepository.deleteAd(adId);
      if (deleted) {
        return ad;
      } else {
        return null;
      }
    } catch (SQLException e) {
      System.err.println("❌ Ошибка при удалении объявления: " + e.getMessage());
      return null;
    }
  }

  @Override
  public Announcement hardDeleteAd(long adId) {
    try {
      Announcement ad = adsRepository.getAdById(adId);
      if (ad == null) {
        System.out.println("❌ Объявление с ID " + adId + " не найдено");
        return null;
      }
      userService.deleteAnnouncementId(ad.getUserId(), adId);

      boolean deleted = adsRepository.hardDeleteAd(adId);
      if (deleted) {
        return ad;
      } else {
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
        System.out.println(ad.toString());
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
  public List<Long> searchAdsByString(List<Long> adsId, String query) throws SQLException {
    if (query == null || query.trim().isEmpty()) {
      return adsId;
    }

    final double SIMILARITY_THRESHOLD = 0.7; // Порог похожести (0-1)
    final int MAX_RESULTS = 100; // Ограничение количества результатов
    return adsId.stream().filter(adId -> {
          Announcement ad = getAd(adId);
          if (ad == null) {
            return false;
          }

          String searchText = (ad.getTitle() + " " + ad.getDescription()).toLowerCase();
          String cleanQuery = query.toLowerCase().trim();

          return LevenshteinSearch.fuzzyContains(searchText, cleanQuery, SIMILARITY_THRESHOLD);
        })
        .limit(MAX_RESULTS)
        .collect(Collectors.toList());
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

  public List<Long> getActiveAdIds() throws SQLException {
    return adsRepository.getActiveAdIds();
  }

  public AdsRepository getAdsRepository() {
    return this.adsRepository;
  }


  public List<byte[]> getAdPhotosBytes(long adId) throws SQLException {
    try {
      List<byte[]> photos = adsRepository.getAdPhotosBytes(adId);
      System.out.println(
          "✅ AdsService loaded " + (photos != null ? photos.size() : 0) + " photos for ad " + adId);
      return photos != null ? photos : new ArrayList<>();
    } catch (Exception e) {
      System.err.println("❌ Error in AdsService.getAdPhotosBytes: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  public void removePhotoFromAd(long adId, int photoIndex) throws SQLException {
    adsRepository.removePhotoFromAd(adId, photoIndex);
  }

  public List<String> getSearchSuggestionsFromActiveAds(String query) throws SQLException {
    List<String> suggestions = new ArrayList<>();

    // Проверяем соединение с БД
    System.out.println("Searching for: " + query);

    String sql = "SELECT DISTINCT title FROM announcements " +
        "WHERE LOWER(title) LIKE LOWER(?) AND status = 'ACTIVE' " +
        "ORDER BY created_at DESC LIMIT 10";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      System.out.println("SQL query: " + sql);
      System.out.println("Parameter: %" + query + "%");

      stmt.setString(1, "%" + query + "%");

      ResultSet rs = stmt.executeQuery();
      int count = 0;
      while (rs.next()) {
        String title = rs.getString("title");
        suggestions.add(title);
        System.out.println("Found suggestion: " + title);
        count++;
      }

      System.out.println("Total suggestions found: " + count);

    } catch (SQLException e) {
      System.err.println("SQL Error in getSearchSuggestionsFromActiveAds: " + e.getMessage());
      throw e;
    }

    return suggestions;
  }

}