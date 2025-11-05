package com.mipt.portal;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.Announcement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.SQLException;

@SpringBootApplication
public class Main implements CommandLineRunner {

  @Autowired
  private AdsRepository adsRepository;

  @Autowired
  private AdsService adsService;

  public static void main(String[] args) {
    System.out.println("🚀 Запуск Portal Application");
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    try {
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
