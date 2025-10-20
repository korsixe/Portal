package com.mipt.portal;

import java.sql.*;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseManager {
  private static final String URL = "jdbc:sqlite:portal.db";

  public static Connection connect() {
    try {
      Connection conn = DriverManager.getConnection(URL);
      System.out.println("✅ Подключение к SQLite установлено!");
      return conn;
    } catch (SQLException e) {
      System.out.println("❌ Ошибка подключения: " + e.getMessage());
      return null;
    }
  }

  // Чтение SQL файла из resources
  private static String readSqlFile(String filePath) {
    try (InputStream inputStream = DatabaseManager.class.getResourceAsStream(filePath)) {
      if (inputStream == null) {
        throw new RuntimeException("Файл не найден: " + filePath);
      }
      return new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
    }
  }

  // Создание всех таблиц
  public static void createTables() {
    String sql = readSqlFile("/sql/create_tables.sql");
    try (Connection conn = connect();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
      System.out.println("✅ Таблицы созданы успешно!");
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при создании таблиц: " + e.getMessage());
    }
  }

  // Вставка тестовых данных
  public static void insertTestData() {
    String sql = readSqlFile("/sql/insert_data.sql");
    try (Connection conn = connect();
        Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
      System.out.println("✅ Тестовые данные добавлены!");
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при добавлении данных: " + e.getMessage());
    }
  }

  // Добавление нового пользователя
  public static void insertUser(String name, String email) {
    String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
    try (Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, name);
      pstmt.setString(2, email);
      pstmt.executeUpdate();
      System.out.println("✅ Пользователь добавлен: " + name);
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при добавлении пользователя: " + e.getMessage());
    }
  }

  // Добавление нового объявления
  public static void insertAd(String title, String description, int userId, double price) {
    String sql = "INSERT INTO ads (title, description, user_id, price, created_at) VALUES (?, ?, ?, ?, datetime('now'))";
    try (Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, title);
      pstmt.setString(2, description);
      pstmt.setInt(3, userId);
      pstmt.setDouble(4, price);
      pstmt.executeUpdate();
      System.out.println("✅ Объявление добавлено: " + title);
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при добавлении объявления: " + e.getMessage());
    }
  }

  // Показать всех пользователей
  public static void selectAllUsers() {
    String sql = "SELECT * FROM users";
    try (Connection conn = connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      System.out.println("\n📋 Список пользователей:");
      System.out.println("ID\tИмя\t\tEmail");
      System.out.println("--\t----\t\t-----");
      while (rs.next()) {
        System.out.println(rs.getInt("id") + "\t" +
            rs.getString("name") + "\t\t" +
            rs.getString("email"));
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при чтении пользователей: " + e.getMessage());
    }
  }

  // Показать все объявления
  public static void selectAllAds() {
    String sql = """
            SELECT ads.*, users.name as user_name 
            FROM ads 
            JOIN users ON ads.user_id = users.id
            ORDER BY ads.created_at DESC
        """;
    try (Connection conn = connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      System.out.println("\n🏷️ Список объявлений:");
      System.out.println("ID\tЗаголовок\tЦена\tАвтор\tДата");
      System.out.println("--\t---------\t----\t----\t----");
      while (rs.next()) {
        System.out.println(rs.getInt("id") + "\t" +
            rs.getString("title") + "\t" +
            rs.getDouble("price") + " руб.\t" +
            rs.getString("user_name") + "\t" +
            rs.getString("created_at").substring(0, 16));
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при чтении объявлений: " + e.getMessage());
    }
  }

  // Поиск объявлений по ключевому слову
  public static void searchAds(String keyword) {
    String sql = """
            SELECT ads.*, users.name as user_name 
            FROM ads 
            JOIN users ON ads.user_id = users.id
            WHERE ads.title LIKE ? OR ads.description LIKE ?
            ORDER BY ads.created_at DESC
        """;
    try (Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, "%" + keyword + "%");
      pstmt.setString(2, "%" + keyword + "%");

      ResultSet rs = pstmt.executeQuery();

      System.out.println("\n🔍 Результаты поиска '" + keyword + "':");
      boolean found = false;
      while (rs.next()) {
        if (!found) {
          System.out.println("ID\tЗаголовок\tЦена\tАвтор");
          System.out.println("--\t---------\t----\t----");
          found = true;
        }
        System.out.println(rs.getInt("id") + "\t" +
            rs.getString("title") + "\t" +
            rs.getDouble("price") + " руб.\t" +
            rs.getString("user_name"));
      }
      if (!found) {
        System.out.println("😞 Объявления не найдены");
      }
    } catch (SQLException e) {
      System.out.println("❌ Ошибка при поиске: " + e.getMessage());
    }
  }
}