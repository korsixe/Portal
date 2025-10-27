package com.mipt.portal;

import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.AdManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
  public static void main(String[] args) {
    System.out.println("🚀 Запуск Portal Application");

    try {
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5432/myproject",
          "myuser",
          "mypassword"
      );

      DatabaseManager dbManager = new DatabaseManager(connection);
      dbManager.createTables();
      System.out.println("✅ Таблицы успешно созданы!");
      dbManager.insertData();

      System.out.println("Теперь давайте создадим объявление");
      AdManager adManager = new AdManager(dbManager);
      Announcement cur = adManager.createAd(dbManager.getUserIdByEmail("shabunina.ao@phystech.edu"));
      cur = adManager.editAd(cur);
      cur = adManager.deleteAd(cur.getId());
    } catch (SQLException e) {
      System.err.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
      e.printStackTrace();
    }
  }
}