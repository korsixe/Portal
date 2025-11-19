package com.mipt.portal.announcement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.portal.database.DatabaseConnection;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdsRepository implements IAdsRepository {

  private Connection connection;
  private ObjectMapper objectMapper;


  public AdsRepository() throws SQLException {
    this.connection = DatabaseConnection.getConnection();
    this.objectMapper = new ObjectMapper();
  }

  public AdsRepository(Connection connection) throws SQLException {
    this.connection = connection;
    this.objectMapper = new ObjectMapper(); // Добавляем инициализацию ObjectMapper
  }

  @Override
  public void deleteData() {
    try {
      String sql = readSqlFile("sql/delete_data.sql");
      executeSql(sql);
      resetSequences();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void resetSequences() throws SQLException {
    String[] sequences = {"users_id_seq", "ads_id_seq", "moderators_id_seq", "comments_id_seq"};

    for (String seq : sequences) {
      try (Statement stmt = connection.createStatement()) {
        stmt.execute("ALTER SEQUENCE IF EXISTS " + seq + " RESTART WITH 1;");
      }
    }
  }

  @Override
  public void createTables() {
    try {
      String sql = readSqlFile("sql/create_tables.sql");
      executeSql(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void insertData() {
    try {
      String sql = readSqlFile("sql/insert_data.sql");
      executeSql(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Long getUserIdByEmail(String email) throws SQLException {
    String sql = "SELECT id FROM users WHERE email = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, email);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return resultSet.getLong("id");
      }
      return null;
    }
  }

  @Override
  public void updateAd(Announcement ad) throws SQLException {
    String sql = """
            UPDATE ads
            SET title = ?, description = ?, category = ?, subcategory = ?, condition = ?,
                price = ?, location = ?, status = ?, updated_at = CURRENT_TIMESTAMP,
                view_count = ?, tags = ?::JSONB, tags_count = ?
            WHERE id = ?
        """;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, ad.getTitle());
      statement.setString(2, ad.getDescription());
      statement.setInt(3, ad.getCategory().ordinal());
      statement.setString(4, ad.getSubcategory());
      statement.setInt(5, ad.getCondition().ordinal());
      statement.setInt(6, ad.getPrice());
      statement.setString(7, ad.getLocation());
      statement.setString(8, ad.getStatus().name());
      statement.setInt(9, ad.getViewCount());

      if (ad.getTags() != null && !ad.getTags().isEmpty()) {
        try {
          String tagsJson = objectMapper.writeValueAsString(ad.getTags());
          System.out.println("Update - Generated JSON: " + tagsJson);
          statement.setString(10, tagsJson); // tags - параметр 10
        } catch (Exception e) {
          System.err.println("❌ Error converting tags to JSON in update: " + e.getMessage());
          String tagsJson = convertTagsToJson(ad.getTags());
          statement.setString(10, tagsJson);
        }
      } else {
        statement.setNull(10, Types.VARCHAR); // tags - параметр 10
      }

      statement.setInt(11, ad.getTagsCount() != null ? ad.getTagsCount() : 0); // tags_count - параметр 11
      statement.setLong(12, ad.getId()); // id - параметр 12

      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Обновление объявления failed, no rows affected.");
      }
    }
  }


  @Override
  public Announcement getAdById(long adId) throws SQLException {
    String sql = """
            SELECT a.*, u.name as user_name
            FROM ads a
            LEFT JOIN users u ON a.user_id = u.id
            WHERE a.id = ?
        """;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, adId);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return mapResultSetToAd(resultSet);
      }
      return null; // объявление не найдено
    }
  }

  @Override
  public List<Long> getAllAdIds() throws SQLException {
    String sql = "SELECT id FROM ads ORDER BY id";

    List<Long> ids = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        ids.add(resultSet.getLong("id"));
      }
    }

    return ids;
  }

  @Override
  public List<Long> getModerAdIds() throws SQLException {
    String sql = "SELECT id FROM ads WHERE status = 'UNDER_MODERATION' ORDER BY id";

    List<Long> ids = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        ids.add(resultSet.getLong("id"));
      }
    }

    return ids;
  }

  @Override
  public List<Long> getActiveAdIds() throws SQLException {
    String sql = "SELECT id FROM ads WHERE status = 'ACTIVE' ORDER BY id";

    List<Long> ids = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        ids.add(resultSet.getLong("id"));
      }
    }

    return ids;
  }

  @Override
  public long saveAd(Announcement ad) throws SQLException {
    String sql = """
            INSERT INTO ads (title, description, category, subcategory, condition, price,
                            location, user_id, status, view_count, tags, tags_count)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::JSONB, ?)
            RETURNING id
        """;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, ad.getTitle());
      statement.setString(2, ad.getDescription());
      statement.setInt(3, ad.getCategory().ordinal());
      statement.setString(4, ad.getSubcategory());
      statement.setInt(5, ad.getCondition().ordinal());
      statement.setInt(6, ad.getPrice());
      statement.setString(7, ad.getLocation());
      statement.setLong(8, ad.getUserId());
      statement.setString(9, ad.getStatus().name());
      statement.setInt(10, ad.getViewCount());

      // Преобразуем список тегов в JSON
      System.out.println("=== DEBUG SAVE AD ===");
      System.out.println("Tags list: " + ad.getTags());

      if (ad.getTags() != null && !ad.getTags().isEmpty()) {
        try {
          String tagsJson = objectMapper.writeValueAsString(ad.getTags());
          System.out.println("Generated JSON: " + tagsJson);
          statement.setString(11, tagsJson); // для saveAd
        } catch (Exception e) {
          System.err.println("❌ Error converting tags to JSON: " + e.getMessage());
          String tagsJson = convertTagsToJson(ad.getTags());
          statement.setString(11, tagsJson);
        }
      } else {
        System.out.println("No tags to save");
        statement.setNull(11, Types.VARCHAR); // для saveAd
      }

      statement.setInt(12, ad.getTagsCount() != null ? ad.getTagsCount() : 0);

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getLong(1);

      }
      throw new SQLException("Failed to get generated ID");
    } catch (Exception e) {
      System.err.println("❌ FULL ERROR in saveAd:");
      e.printStackTrace();
      throw new SQLException("Error saving ad with tags", e);
    }
  }

  @Override
  public boolean deleteAd(long adId) throws SQLException {
    String sql = "UPDATE ads SET status = 'DELETED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, adId);
      int affectedRows = statement.executeUpdate();
      return affectedRows > 0;
    }
  }

  @Override
  public boolean hardDeleteAd(long adId) throws SQLException {
    String sql = "DELETE FROM ads WHERE id = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, adId);
      int affectedRows = statement.executeUpdate();
      return affectedRows > 0;
    }
  }

  // метод для сохранения фото
  private void saveAdPhotos(long adId, List<File> photos) throws SQLException {
  }

  // метод для загрузки фото объявления
  public List<String> getAdPhotos(long adId) throws SQLException {
    return null;
  }

  private Announcement mapResultSetToAd(ResultSet resultSet) throws SQLException {
    // Создаем объявление с базовыми полями
    Announcement ad = new Announcement(
        resultSet.getString("title"),
        resultSet.getString("description"),
        Category.values()[resultSet.getInt("category")],
        Condition.values()[resultSet.getInt("condition")],
        resultSet.getInt("price"),
        resultSet.getString("location"),
        resultSet.getLong("user_id")
    );

    // Устанавливаем дополнительные поля
    ad.setId(resultSet.getLong("id"));
    ad.setSubcategory(resultSet.getString("subcategory"));
    ad.setViewCount(resultSet.getInt("view_count"));

    // Устанавливаем статус
    String statusStr = resultSet.getString("status");
    try {
      AdvertisementStatus status = AdvertisementStatus.valueOf(statusStr);
      ad.setStatus(status);
    } catch (IllegalArgumentException e) {
      ad.setStatus(AdvertisementStatus.DRAFT);
    }

    // 🔥 ИСПРАВЛЕННАЯ ЧАСТЬ: Правильное чтение тегов из JSON
    String tagsJson = resultSet.getString("tags");
    if (tagsJson != null && !tagsJson.trim().isEmpty()) {
      try {
        System.out.println("📥 Reading tags from DB: " + tagsJson);

        // Парсим JSON массив обратно в List<String>
        List<String> tags = objectMapper.readValue(
          tagsJson,
          objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        ad.setTags(tags);

        System.out.println("✅ Successfully parsed " + tags.size() + " tags");

      } catch (Exception e) {
        System.err.println("❌ Error parsing tags JSON from DB: " + e.getMessage());
        System.err.println("❌ Raw JSON: " + tagsJson);
        e.printStackTrace();
        ad.setTags(new ArrayList<>()); // Fallback: пустой список
      }
    } else {
      ad.setTags(new ArrayList<>());
    }

    // Обрабатываем фото
    String photosString = resultSet.getString("photos");
    if (photosString != null && !photosString.trim().isEmpty()) {
      List<File> photos = new ArrayList<>();
      String[] photoPaths = photosString.split("\\s*,\\s*");
      for (String photoPath : photoPaths) {
        File photoFile = new File(photoPath);
        if (photoFile.exists()) {
          photos.add(photoFile);
        }
      }
      ad.setPhotos(photos);
    } else {
      ad.setPhotos(new ArrayList<>());
    }

    // Устанавливаем даты
    Timestamp createdAt = resultSet.getTimestamp("created_at");
    Timestamp updatedAt = resultSet.getTimestamp("updated_at");
    if (createdAt != null) {
      ad.setCreatedAt(createdAt.toInstant());
    }
    if (updatedAt != null) {
      ad.setUpdatedAt(updatedAt.toInstant());
    }

    return ad;
  }

  private String readSqlFile(String filename) {
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

      if (inputStream != null) {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }

      Path path = Paths.get("~Portal/src/main/resources/sql/" + filename);
      if (Files.exists(path)) {
        return Files.readString(path);
      }

      throw new RuntimeException("Файл не найден: " + filename);

    } catch (IOException e) {
      throw new RuntimeException("Ошибка чтения файла: " + filename, e);
    }
  }

  private void executeSql(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      String[] sqlCommands = sql.split(";");
      for (String command : sqlCommands) {
        if (!command.trim().isEmpty()) {
          statement.execute(command.trim());
        }
      }
    }
  }

  private String convertTagsToJson(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return "[]";
    }

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < tags.size(); i++) {
      if (i > 0) json.append(",");
      // Правильное экранирование для JSON
      String escaped = tags.get(i)
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
      json.append("\"").append(escaped).append("\"");
    }
    json.append("]");

    System.out.println("Fallback JSON: " + json.toString());
    return json.toString();
  }
}