package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import com.mipt.portal.announcement.AdvertisementStatus;
import com.mipt.portal.announcementContent.tags.TagSelector;
import com.mipt.portal.users.User;

import java.sql.SQLException;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  private TagSelector tagSelector;
  private ObjectMapper objectMapper;
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

      this.tagSelector = new TagSelector();
      this.objectMapper = new ObjectMapper();
      System.out.println("✅ TagSelector и ObjectMapper созданы успешно");

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

  private void testTagsLoading() {
    try {
      System.out.println("🧪 Testing tags loading during init...");
      List<Map<String, Object>> testTags = tagSelector.getTagsWithValues();
      System.out.println("✅ INIT TEST: Loaded " + testTags.size() + " tags");
      if (!testTags.isEmpty()) {
        System.out.println("✅ INIT TEST: First tag - " + testTags.get(0).get("name"));
      }
    } catch (Exception e) {
      System.err.println("❌ INIT TEST: Failed to load tags: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
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
      String selectedTagsJson = request.getParameter("selectedTags");

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
      Category category;
      try {
        category = Category.fromDisplayName(categoryStr.trim());
      } catch (Exception e) {
        request.setAttribute("error", "Некорректная категория: " + categoryStr);
        request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
        return;
      }

      Condition condition = Condition.valueOf(conditionStr);

      // Обрабатываем цену
      int price = processPrice(priceType, priceStr);
      if (price == Integer.MIN_VALUE) {
        request.setAttribute("error", "Некорректная цена");
        request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
        return;
      }

      HttpSession session = request.getSession();
      User user = (User) session.getAttribute("user");
      if (user == null) {
        response.sendRedirect("login.jsp");
        return;
      }
      Long userId = user.getId();

      // Обрабатываем теги
      List<String> selectedTagsForAnnouncement = new ArrayList<>();
      List<Map<String, Object>> tagSelectionsForDB = new ArrayList<>();

      if (selectedTagsJson != null && !selectedTagsJson.trim().isEmpty()) {
        try {
          List<Map<String, Object>> tagSelections = objectMapper.readValue(
            selectedTagsJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
          );

          // Преобразуем для Announcement и для БД
          for (Map<String, Object> tagSelection : tagSelections) {
            String tagName = (String) tagSelection.get("tagName");
            String valueName = (String) tagSelection.get("valueName");
            if (tagName != null && valueName != null) {
              // Для Announcement (старый формат)
              selectedTagsForAnnouncement.add(tagName + ": " + valueName);
              // Для сохранения в БД (новый формат)
              tagSelectionsForDB.add(tagSelection);
            }
          }
        } catch (Exception e) {
          System.err.println("❌ Error parsing tags JSON: " + e.getMessage());
          // Если JSON не парсится, используем старый формат
          String oldTags = request.getParameter("tags");
          if (oldTags != null && !oldTags.trim().isEmpty()) {
            String[] tagsArray = oldTags.split("\\s*,\\s*");
            for (String tag : tagsArray) {
              String trimmedTag = tag.trim();
              if (!trimmedTag.isEmpty()) {
                selectedTagsForAnnouncement.add(trimmedTag);
              }
            }
          }
        }
      }

      List<File> uploadedPhotos = new ArrayList<>(); // Обрабатываем загрузку фотографий - Лиза О

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
          selectedTagsForAnnouncement,
          "publish".equals(action) ? AdvertisementStatus.UNDER_MODERATION
              : AdvertisementStatus.DRAFT
      );

      request.setAttribute("announcement", ad);
      request.getRequestDispatcher("/successful-create-ad.jsp").forward(request, response);
      // Сохраняем теги в БД
      if (!tagSelectionsForDB.isEmpty()) {
        try {
          tagSelector.saveAdTags(ad.getId(), tagSelectionsForDB);
          System.out.println("✅ Tags saved to database for ad " + ad.getId());
        } catch (SQLException e) {
          System.err.println("❌ Error saving tags to database: " + e.getMessage());
          // Продолжаем выполнение, даже если теги не сохранились в БД
        }
      }

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