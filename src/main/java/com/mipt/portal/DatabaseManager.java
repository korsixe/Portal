package com.mipt.portal;

import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;

public class DatabaseManager implements IDatabaseManager{

  private Connection connection;

  public DatabaseManager(Connection connection) {
    this.connection = connection;
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

    try {
      String sql = readSqlFile("sql/insert_data_ad.sql");
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
      return null; // пользователь не найден
    }
  }

  @Override
  public void updateAd(Announcement ad) throws SQLException {
    String sql = """
        UPDATE ads 
        SET title = ?, description = ?, category = ?, condition = ?, 
            price = ?, location = ?, status = ?, updated_at = CURRENT_TIMESTAMP,
            view_count = ?
        WHERE id = ?
    """;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, ad.getTitle());
      statement.setString(2, ad.getDescription());
      statement.setInt(3, ad.getCategory().getIndex());
      statement.setInt(4, ad.getCondition().getIndex());
      statement.setInt(5, ad.getPrice());
      statement.setString(6, ad.getLocation());
      statement.setString(7, ad.getStatus());
      statement.setInt(8, ad.getViewCount());
      statement.setLong(9, ad.getId());

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
  public long saveAd(Announcement ad) throws SQLException {
    String sql = """
            INSERT INTO ads (title, description, category, condition, price, location, user_id, status, view_count, photo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, ad.getTitle());
      statement.setString(2, ad.getDescription());
      statement.setInt(3, ad.getCategory().getIndex());
      statement.setInt(4, ad.getCondition().getIndex());
      statement.setInt(5, ad.getPrice());
      statement.setString(6, ad.getLocation());
      statement.setLong(7, ad.getUserId());
      statement.setString(8, ad.getStatus());
      statement.setInt(9, ad.getViewCount());


      if (ad.getPhotos() != null) {
        //statement.setBytes(10, ad.getPhotos());
      } else {
        statement.setNull(10, Types.BINARY);
      }

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        long generatedId = resultSet.getLong(1);

        //updateUserAdList(ad.getUserId(), generatedId); - часть Лизы

        return generatedId;
      }
      throw new SQLException("Failed to get generated ID");
    }
  }

  @Override
  public boolean deleteAd(long adId) throws SQLException {
    String sql = "DELETE FROM ads WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, adId);
      int affectedRows = statement.executeUpdate();
      return affectedRows > 0;
    }
  }


  private Announcement mapResultSetToAd(ResultSet resultSet) throws SQLException {
    Announcement ad = new Announcement(
        resultSet.getString("title"),
        resultSet.getString("description"),
        Category.getByNumber(resultSet.getInt("category")),
        Condition.getByNumber(resultSet.getInt("condition")),
        resultSet.getInt("price"),
        resultSet.getString("location"),
        resultSet.getLong("user_id"),
        resultSet.getString("status")
    );

    ad.setId(resultSet.getLong("id"));
    ad.setViewCount(resultSet.getInt("view_count"));

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
}
