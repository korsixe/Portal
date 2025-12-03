package com.mipt.portal;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.users.service.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    try {
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5433/myproject",
          "myuser",
          "mypassword"
      );
      AdsRepository adsRepository = new AdsRepository(connection);
      UserService userService = new UserService();
      AdsService adsService = new AdsService(adsRepository, userService);

      adsRepository.createTables();
      adsRepository.insertData();
      System.out.println("✅ Тестовые данные добавлены!");

      List<Long> hehehe = adsRepository.getModerAdIds();
      for (int i = 0; i < hehehe.size(); ++i) {
        System.out.println(hehehe.get(i));
      }

      System.out.println("✅ Приложение успешно завершило работу!");
    } catch (SQLException e) {
      System.err.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
