package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import com.mipt.portal.announcement.AdvertisementStatus;

import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@WebServlet("/create-ad")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 10,      // 10 MB max file size
    maxRequestSize = 1024 * 1024 * 50,   // 50 MB max request size
    fileSizeThreshold = 1024 * 1024      // 1 MB size threshold
)
public class CreateAdServlet extends HttpServlet {

  private AdsService adsService;
  private static final String UPLOAD_DIR = "uploads";
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
  private static final Logger logger = Logger.getLogger(CreateAdServlet.class.getName());

  @Override
  public void init() throws ServletException {
    try {
      System.out.println("🚀 ========== НАЧАЛО ИНИЦИАЛИЗАЦИИ ==========");
      System.out.println("📦 Создаем AdsRepository...");

      System.out.println("📦 Создаем AdsService...");
      this.adsService = new AdsService();
      System.out.println("✅ AdsService создан успешно");

      logger.info("AdsService initialized successfully");
      System.out.println("🎉 ========== ИНИЦИАЛИЗАЦИЯ ЗАВЕРШЕНА ==========");

    } catch (Exception e) {
      System.err.println("❌ ========== ОШИБКА ИНИЦИАЛИЗАЦИИ ==========");
      System.err.println("❌ Ошибка: " + e.getMessage());
      e.printStackTrace();
      logger.severe("Error initializing AdsService: " + e.getMessage());
      throw new ServletException("Error initializing AdsService", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Просто показываем форму создания объявления
    request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      // Получаем параметры из формы
      String title = request.getParameter("title");
      String description = request.getParameter("description");
      String categoryStr = request.getParameter("category");
      String subcategory = request.getParameter("subcategory");
      String conditionStr = request.getParameter("condition");
      String location = request.getParameter("location");
      String priceType = request.getParameter("priceType");
      String priceStr = request.getParameter("price");
      String action = request.getParameter("action");
      String tags = request.getParameter("tags");

      // Валидация обязательных полей
      if (title == null || title.trim().isEmpty() ||
          description == null || description.trim().isEmpty() ||
          categoryStr == null || categoryStr.trim().isEmpty() ||
          conditionStr == null || conditionStr.trim().isEmpty() ||
          location == null || location.trim().isEmpty()) {

        request.setAttribute("error", "Пожалуйста, заполните все обязательные поля");
        request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
        return;
      }

      // Преобразуем категорию и состояние
      Category category = Category.valueOf(categoryStr);
      Condition condition = Condition.valueOf(conditionStr);

      // Обрабатываем цену
      int price = processPrice(priceType, priceStr);
      if (price == Integer.MIN_VALUE) {
        request.setAttribute("error", "Некорректная цена");
        request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
        return;
      }

      // Получаем ID пользователя из сессии
      //HttpSession session = request.getSession();
      //Long userId = (Long) session.getAttribute("userId");

      Long userId = adsService.getUserIdByEmail("shabunina.ao@phystech.edu");

      // Если пользователь не авторизован, используем заглушку
      if (userId == null) {
        userId = 1L; // В реальном приложении нужно перенаправить на страницу входа
      }

      List<File> uploadedPhotos = new ArrayList<>(); // Обрабатываем загрузку фотографий - Лиза О
      List<String> tag = new ArrayList<>(); // Обрабатываем теги - Лиза О

      Announcement ad = adsService.createAd(
          userId,
          title,
          description,
          category,
          subcategory,
          condition,
          price,
          location,
          uploadedPhotos,
          tag,
          "publish".equals(action) ? AdvertisementStatus.UNDER_MODERATION : AdvertisementStatus.DRAFT
      );

      request.setAttribute("success",
          String.format("Объявление успешно создано! ID: %d", ad.getId()));

      // Очищаем форму после успешного создания
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);

    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Некорректные данные: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    } catch (IllegalStateException e) {
      request.setAttribute("error", "Ошибка статуса: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    } catch (Exception e) {
      request.setAttribute("error", "Произошла ошибка при создании объявления: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    }
  }

  private int processPrice(String priceType, String priceStr) {
    if (priceType == null) {
      return -1;
    }

    switch (priceType) {
      case "fixed":
        try {
          int price = Integer.parseInt(priceStr);
          return price > 0 ? price : Integer.MIN_VALUE;
        } catch (NumberFormatException e) {
          return Integer.MIN_VALUE;
        }
      case "free":
        return 0;
      case "negotiable":
      default:
        return -1;
    }
  }

}