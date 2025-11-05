package com.mipt.portal;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
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
      AdsRepository adsRepository = new AdsRepository(connection);
      AdsService adsService = new AdsService(adsRepository);

      adsRepository.createTables();
      System.out.println("✅ Таблицы успешно созданы!");

      adsRepository.insertData();
      System.out.println("✅ Тестовые данные добавлены!");

      System.out.println("Теперь давайте создадим объявление");

      Long userId = adsRepository.getUserIdByEmail("shabunina.ao@phystech.edu");
      if (userId == null) {
        System.out.println("❌ Пользователь не найден!");
        return;
      }

      Announcement cur = adsService.createAd(userId);
      if (cur != null) {
        cur = adsService.editAd(cur);
        cur = adsService.deleteAd(cur.getId());
        adsRepository.hardDeleteAd(cur.getId());
      }

      System.out.println("✅ Приложение успешно завершило работу!");
    } catch (SQLException e) {
      System.err.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
