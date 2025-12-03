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

      this.adsService = new AdsService();
      this.tagSelector = new TagSelector();
      this.objectMapper = new ObjectMapper();

    } catch (Exception e) {
      System.err.println("❌ ========== ОШИБКА ИНИЦИАЛИЗАЦИИ ==========");
      System.err.println("❌ Ошибка: " + e.getMessage());
      logger.severe("Error initializing AdsService: " + e.getMessage());
      throw new ServletException("Error initializing AdsService", e);
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

    // ОБЪЯВЛЯЕМ uploadedPhotos ЗДЕСЬ ДЛЯ ДОСТУПА В БЛОКЕ CATCH
    List<File> uploadedPhotos = new ArrayList<>();

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
      long userId = user.getId();

      // ОБРАБОТКА ФОТО ДО СОЗДАНИЯ ОБЪЯВЛЕНИЯ
      uploadedPhotos = processUploadedPhotos(request);
      System.out.println("📸 Processed " + uploadedPhotos.size() + " uploaded photos");

      // СОЗДАЕМ ОБЪЯВЛЕНИЕ С ПУСТЫМИ ФОТО И ТЕГАМИ
      Announcement ad = adsService.createAd(
        userId,
        title,
        description,
        category,
        subcategory,
        condition,
        price,
        location,
        new ArrayList<>(), // ПУСТОЙ список фото на начальном этапе
        new ArrayList<>(), // ПУСТОЙ список тегов на начальном этапе
        "publish".equals(action) ? AdvertisementStatus.UNDER_MODERATION
          : AdvertisementStatus.DRAFT
      );

      System.out.println("✅ Announcement created with ID: " + ad.getId());

      // ПОСЛЕ СОЗДАНИЯ ОБЪЯВЛЕНИЯ СОХРАНЯЕМ ФОТОГРАФИИ В БАЗУ ДАННЫХ
      if (ad.getId() != 0 && !uploadedPhotos.isEmpty()) {
        System.out.println("💾 Starting photo save process for ad " + ad.getId());

        // Создаем список байтовых массивов
        List<byte[]> photoBytes = new ArrayList<>();
        for (File photo : uploadedPhotos) {
          try {
            byte[] fileData = Files.readAllBytes(photo.toPath());
            photoBytes.add(fileData);
            System.out.println("✅ Photo read: " + photo.getName() + " (" + fileData.length + " bytes)");
          } catch (IOException e) {
            System.err.println("❌ Error reading photo file: " + e.getMessage());
          }
        }

        // Сохраняем фото в базу данных
        try {
          adsService.getAdsRepository().saveAdPhotosBytes(ad.getId(), photoBytes);
          System.out.println("✅ Photos saved to database for ad " + ad.getId());
        } catch (SQLException e) {
          System.err.println("❌ Error saving photos to database: " + e.getMessage());
        }

        // Удаляем временные файлы после сохранения в БД
        for (File photo : uploadedPhotos) {
          try {
            if (photo.exists()) {
              Files.delete(photo.toPath());
              System.out.println("🗑️ Temporary file deleted: " + photo.getName());
            }
          } catch (IOException e) {
            System.err.println("⚠️ Could not delete temporary file: " + e.getMessage());
          }
        }
      }

      // ПОСЛЕ СОХРАНЕНИЯ ФОТО СОХРАНЯЕМ ТЕГИ
      if (selectedTagsJson != null && !selectedTagsJson.trim().isEmpty()) {
        try {
          List<Map<String, Object>> tagSelections = objectMapper.readValue(
            selectedTagsJson,
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
          );

          // Сохраняем теги в БД
          if (!tagSelections.isEmpty()) {
            try {
              tagSelector.saveAdTags(ad.getId(), tagSelections);
              System.out.println("✅ Tags saved to database for ad " + ad.getId());

              // ОБНОВЛЯЕМ ТЕГИ В ОСНОВНОЙ ЗАПИСИ ОБЪЯВЛЕНИЯ
              List<String> selectedTagsForAnnouncement = new ArrayList<>();
              for (Map<String, Object> tagSelection : tagSelections) {
                String tagName = (String) tagSelection.get("tagName");
                String valueName = (String) tagSelection.get("valueName");
                if (tagName != null && valueName != null) {
                  String tagString = tagName + ": " + valueName;
                  selectedTagsForAnnouncement.add(tagString);
                }
              }

              // Обновляем объявление с тегами
              ad.setTags(selectedTagsForAnnouncement);
              ad.setTagsCount(selectedTagsForAnnouncement.size());
              adsService.editAd(ad);
              System.out.println("✅ Announcement updated with tags");

            } catch (SQLException e) {
              System.err.println("❌ Error saving tags to database: " + e.getMessage());
              // Продолжаем выполнение, даже если теги не сохранились в БД
            }
          }

        } catch (Exception e) {
          System.err.println("❌ Error parsing tags JSON: " + e.getMessage());
        }
      }

      request.setAttribute("announcement", ad);
      request.getRequestDispatcher("/successful-create-ad.jsp").forward(request, response);

    } catch (IllegalArgumentException e) {
      System.err.println("❌ IllegalArgumentException: " + e.getMessage());

      // ОЧИСТКА ВРЕМЕННЫХ ФАЙЛОВ ПРИ ОШИБКЕ
      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Некорректные данные: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    } catch (IllegalStateException e) {
      System.err.println("❌ IllegalStateException: " + e.getMessage());

      // ОЧИСТКА ВРЕМЕННЫХ ФАЙЛОВ ПРИ ОШИБКЕ
      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Ошибка статуса: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    } catch (Exception e) {
      System.err.println("❌ General Exception: " + e.getMessage());
      e.printStackTrace();

      // ОЧИСТКА ВРЕМЕННЫХ ФАЙЛОВ ПРИ ОШИБКЕ
      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Произошла ошибка при создании объявления: " + e.getMessage());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    }
  }

  // ДОБАВЬТЕ ЭТОТ ВСПОМОГАТЕЛЬНЫЙ МЕТОД В КЛАСС
  private void cleanupUploadedPhotos(List<File> uploadedPhotos) {
    for (File photo : uploadedPhotos) {
      try {
        if (photo.exists()) {
          Files.delete(photo.toPath());
          System.out.println("🗑️ Cleaned up temporary file after error: " + photo.getName());
        }
      } catch (IOException ioException) {
        System.err.println("⚠️ Could not delete temporary file during cleanup: " + ioException.getMessage());
      }
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

  // МЕТОД ДЛЯ ОБРАБОТКИ ЗАГРУЖЕННЫХ ФОТОГРАФИЙ
  private List<File> processUploadedPhotos(HttpServletRequest request) throws IOException, ServletException {
    List<File> uploadedPhotos = new ArrayList<>();

    // Создаем директорию для загрузок, если её нет
    String appPath = request.getServletContext().getRealPath("");
    String uploadPath = appPath + File.separator + UPLOAD_DIR;

    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
      System.out.println("✅ Created upload directory: " + uploadPath);
    }

    // Обрабатываем каждое загруженное фото
    for (Part part : request.getParts()) {
      if (part.getName().equals("photos") && part.getSize() > 0) {
        String fileName = extractFileName(part);

        // Проверяем расширение файла
        if (isValidFileExtension(fileName)) {
          // Создаем безопасное имя файла
          String safeFileName = System.currentTimeMillis() + "_" +
            fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
          String filePath = uploadPath + File.separator + safeFileName;
          File photoFile = new File(filePath);

          // Сохраняем файл на диск
          try (InputStream input = part.getInputStream()) {
            Files.copy(input, photoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            uploadedPhotos.add(photoFile);
            System.out.println("✅ Photo saved: " + filePath + " (" + part.getSize() + " bytes)");
          } catch (Exception e) {
            System.err.println("❌ Error saving photo: " + e.getMessage());
          }
        } else {
          System.err.println("❌ Invalid file extension: " + fileName);
        }
      }
    }

    System.out.println("📸 Total processed photos: " + uploadedPhotos.size());
    return uploadedPhotos;
  }


  // Вспомогательные методы
  private String extractFileName(Part part) {
    String contentDisp = part.getHeader("content-disposition");
    String[] items = contentDisp.split(";");
    for (String s : items) {
      if (s.trim().startsWith("filename")) {
        return s.substring(s.indexOf("=") + 2, s.length() - 1);
      }
    }
    return "";
  }

  private boolean isValidFileExtension(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      return false;
    }
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    return ALLOWED_EXTENSIONS.contains(extension);
  }




}