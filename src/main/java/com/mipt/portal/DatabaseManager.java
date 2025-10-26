package com.mipt.portal;

import com.mipt.portal.ad.Ad;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;

public class DatabaseManager {

  private Connection connection;

  public DatabaseManager(Connection connection) {
    this.connection = connection;
  }

  public void createTables() {
    try {
      String sql = readSqlFile("sql/create_tables.sql");
      executeSql(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


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

  public long saveAd(Ad ad) throws SQLException {
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


      if (ad.getPhoto() != null) {
        //statement.setBytes(10, ad.getPhoto());
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
