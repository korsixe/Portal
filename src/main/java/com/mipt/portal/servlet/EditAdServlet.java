package com.mipt.portal.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import com.mipt.portal.announcementContent.MediaManager;
import com.mipt.portal.announcementContent.tags.TagSelector;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Boolean.FALSE;

@WebServlet("/edit-ad")
@MultipartConfig(
  maxFileSize = 1024 * 1024 * 10,
  maxRequestSize = 1024 * 1024 * 50,
  fileSizeThreshold = 1024 * 1024
)
public class EditAdServlet extends HttpServlet {

  private AdsService adsService;
  private TagSelector tagSelector;
  private ObjectMapper objectMapper;
  private static final String UPLOAD_DIR = "uploads";
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg",  "png", "gif");

  @Override
  public void init() throws ServletException {
    try {
      System.out.println("🚀 ========== НАЧАЛО ИНИЦИАЛИЗАЦИИ EDIT SERVLET ==========");
      this.adsService = new AdsService();
      this.tagSelector = new TagSelector();
      this.objectMapper = new ObjectMapper();
      System.out.println("🎉 ========== ИНИЦИАЛИЗАЦИЯ EDIT SERVLET ЗАВЕРШЕНА ==========");
    } catch (Exception e) {
      System.err.println("❌ ========== ОШИБКА ИНИЦИАЛИЗАЦИИ EDIT SERVLET ==========");
      System.err.println("❌ Ошибка: " + e.getMessage());
      throw new ServletException("Error initializing EditAdServlet", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String action = request.getParameter("action");

    if ("removePhoto".equals(action)) {
      String adIdStr = request.getParameter("adId");
      String photoIndexStr = request.getParameter("photoIndex");

      // Удаляем фото
      handleRemovePhoto(adIdStr, photoIndexStr, response);
      return; // Важно: не продолжаем дальше
    }

    try {
      String adIdStr = request.getParameter("adId");
      String categoryParam = request.getParameter("category");

      if ("removePhoto".equals(action)) {
        String photoIndexStr = request.getParameter("photoIndex");
        if (adIdStr != null && photoIndexStr != null) {
          handleRemovePhoto(adIdStr, photoIndexStr, response);
          return;
        }
      }

      if (adIdStr == null) {
        response.sendRedirect("dashboard.jsp");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      Announcement announcement = adsService.getAd(adId);

      if (announcement == null) {
        request.setAttribute("error", "Объявление не найдено");
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        return;
      }

      // Обработка быстрых действий
      if ("toDraft".equals(action)) {
        adsService.saveAsDraft(announcement);

        // Подготовка данных для страницы успеха
        request.setAttribute("announcement", announcement);
        request.setAttribute("action", "statusChange");
        request.setAttribute("success", "Объявление сохранено как черновик");

        // Создаем список изменений
        List<String> changes = new ArrayList<>();
        changes.add("Статус изменен на 'Черновик'");
        request.setAttribute("changes", changes);

        // Перенаправляем на страницу успешного редактирования
        request.getRequestDispatcher("/successful-edit-ad.jsp").forward(request, response);
        return; // ВАЖНО: добавляем return чтобы код не продолжал выполняться дальше
      }

      // Сохраняем параметры категории из запроса
      if (categoryParam != null) {
        request.setAttribute("selectedCategory", categoryParam);
      }
      if (request.getParameter("subcategory") != null) {
        request.setAttribute("selectedSubcategory", request.getParameter("subcategory"));
      }

      request.setAttribute("announcement", announcement);

      // Загружаем теги
      request.setAttribute("availableTags", tagSelector.getTagsWithValues());
      request.setAttribute("currentTags", tagSelector.getTagsForAd(adId));

      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);

    } catch (Exception e) {
      request.setAttribute("error", "Ошибка при загрузке объявления: " + e.getMessage());
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    System.out.println("🔥🔥🔥 ========== EDIT-AD DO POST START ==========");

    String action = request.getParameter("action");

    if ("removePhoto".equals(action)) {
      String adIdStr = request.getParameter("adId");
      String photoIndexStr = request.getParameter("photoIndex");

      // Удаляем фото
      handleRemovePhoto(adIdStr, photoIndexStr, response);
      return; // Важно: не продолжаем дальше
    }

    List<File> uploadedPhotos = new ArrayList<>();

    try {
      String adIdStr = request.getParameter("adId");
      if (adIdStr == null) {
        response.sendRedirect("dashboard.jsp");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      Announcement existingAd = adsService.getAd(adId);

      if (existingAd == null) {
        request.setAttribute("error", "Объявление не найдено");
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        return;
      }

      if (FALSE) {
        request.setAttribute("error", "Это объявление нельзя редактировать в текущем статусе");
        loadTagsForPage(request, adId);
        request.setAttribute("announcement", existingAd);
        request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
        return;
      }

      // Получаем данные из формы
      String title = request.getParameter("title");
      String description = request.getParameter("description");
      String categoryStr = request.getParameter("category");
      String subcategory = request.getParameter("subcategory");
      String conditionStr = request.getParameter("condition");
      String location = request.getParameter("location");
      String priceType = request.getParameter("priceType");
      String priceStr = request.getParameter("price");
      String selectedTagsJson = request.getParameter("selectedTags");



      // Валидация обязательных полей
      if (title == null || title.trim().isEmpty() ||
        description == null || description.trim().isEmpty() ||
        categoryStr == null || categoryStr.trim().isEmpty() ||
        conditionStr == null || conditionStr.trim().isEmpty() ||
        location == null || location.trim().isEmpty()) {

        request.setAttribute("error", "Пожалуйста, заполните все обязательные поля");
        loadTagsForPage(request, adId);
        request.setAttribute("announcement", existingAd);
        request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
        return;
      }

      // Преобразуем категорию и состояние
      Category category;
      try {
        category = Category.fromDisplayName(categoryStr.trim());
      } catch (Exception e) {
        request.setAttribute("error", "Некорректная категория: " + categoryStr);
        loadTagsForPage(request, adId);
        request.setAttribute("announcement", existingAd);
        request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
        return;
      }

      Condition condition = Condition.valueOf(conditionStr);

      // Обрабатываем цену
      int price = processPrice(priceType, priceStr);
      if (price == Integer.MIN_VALUE) {
        request.setAttribute("error", "Некорректная цена");
        loadTagsForPage(request, adId);
        request.setAttribute("announcement", existingAd);
        request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
        return;
      }

      // ОБРАБОТКА ФОТО ДО ОБНОВЛЕНИЯ ОБЪЯВЛЕНИЯ
      uploadedPhotos = processUploadedPhotos(request);
      System.out.println("📸 Processed " + uploadedPhotos.size() + " uploaded photos for edit");

      // Обновляем объявление
      existingAd.setTitle(title);
      existingAd.setDescription(description);
      existingAd.setCategory(category);
      existingAd.setSubcategory(subcategory);
      existingAd.setCondition(condition);
      existingAd.setLocation(location);
      existingAd.setPrice(price);

      // ПОСЛЕ ОБНОВЛЕНИЯ ОСНОВНЫХ ДАННЫХ СОХРАНЯЕМ ФОТОГРАФИИ В БАЗУ ДАННЫХ
      if (!uploadedPhotos.isEmpty()) {
        System.out.println("💾 Starting photo save process for ad " + adId);

        // Получаем текущие фото из базы
        List<byte[]> currentPhotos = adsService.getAdPhotosBytes(adId);
        List<byte[]> allPhotos = new ArrayList<>(currentPhotos);

        // Добавляем новые фото
        for (File photo : uploadedPhotos) {
          try {
            byte[] fileData = Files.readAllBytes(photo.toPath());
            allPhotos.add(fileData);
            System.out.println("✅ New photo read: " + photo.getName() + " (" + fileData.length + " bytes)");
          } catch (IOException e) {
            System.err.println("❌ Error reading new photo file: " + e.getMessage());
          }
        }

        // Сохраняем все фото в базу данных
        try {
          adsService.getAdsRepository().saveAdPhotosBytes(adId, allPhotos);
          System.out.println("✅ All photos saved to database for ad " + adId + " (total: " + allPhotos.size() + " photos)");
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
              tagSelector.saveAdTags(adId, tagSelections);
              System.out.println("✅ Tags saved to database for ad " + adId);

              // ОБНОВЛЯЕМ ТЕГИ В ОСНОВНОМ ОБЪЕКТЕ ОБЪЯВЛЕНИЯ
              List<String> selectedTagsForAnnouncement = new ArrayList<>();
              for (Map<String, Object> tagSelection : tagSelections) {
                String tagName = (String) tagSelection.get("tagName");
                String valueName = (String) tagSelection.get("valueName");
                if (tagName != null && valueName != null) {
                  String tagString = tagName + ": " + valueName;
                  selectedTagsForAnnouncement.add(tagString);
                }
              }

              existingAd.setTags(selectedTagsForAnnouncement);
              existingAd.setTagsCount(selectedTagsForAnnouncement.size());

            } catch (SQLException e) {
              System.err.println("❌ Error saving tags to database: " + e.getMessage());
              // Продолжаем выполнение, даже если теги не сохранились в БД
            }
          }

        } catch (Exception e) {
          System.err.println("❌ Error parsing tags JSON: " + e.getMessage());
        }
      }

      // Обновляем статус и сохраняем объявление
      if ("publish".equals(action)) {
        adsService.sendToModeration(existingAd);
      } else {
        adsService.saveAsDraft(existingAd);
      }

      adsService.editAd(existingAd);

// Подготовка данных для страницы успеха
      request.setAttribute("announcement", existingAd);
      request.setAttribute("success", "Объявление успешно обновлено!");

// Определяем тип действия
      if ("publish".equals(action)) {
        request.setAttribute("action", "statusChange");
      } else {
        request.setAttribute("action", "edit");
      }

      List<String> changes = new ArrayList<>();
      changes.add("Обновлена информация об объявлении");

      if (!uploadedPhotos.isEmpty()) {
        changes.add("Добавлены новые фотографии (" + uploadedPhotos.size() + " шт.)");
      }

      if (selectedTagsJson != null && !selectedTagsJson.trim().isEmpty()) {
        changes.add("Обновлены теги");
      }

      if ("publish".equals(action)) {
        changes.add("Статус изменен на 'На модерации'");
      }

      request.setAttribute("changes", changes);

      request.getRequestDispatcher("/successful-edit-ad.jsp").forward(request, response);

    } catch (IllegalArgumentException e) {
      System.err.println("❌ IllegalArgumentException: " + e.getMessage());

      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Некорректные данные: " + e.getMessage());
      handleError(request, response);
    } catch (IllegalStateException e) {
      System.err.println("❌ IllegalStateException: " + e.getMessage());

      // ОЧИСТКА ВРЕМЕННЫХ ФАЙЛОВ ПРИ ОШИБКЕ
      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Ошибка статуса: " + e.getMessage());
      handleError(request, response);
    } catch (Exception e) {
      System.err.println("❌ General Exception: " + e.getMessage());
      e.printStackTrace();

      // ОЧИСТКА ВРЕМЕННЫХ ФАЙЛОВ ПРИ ОШИБКЕ
      cleanupUploadedPhotos(uploadedPhotos);

      request.setAttribute("error", "Произошла ошибка при обновлении объявления: " + e.getMessage());
      handleError(request, response);
    }
  }

  private void handleError(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    try {
      String adIdStr = request.getParameter("adId");
      if (adIdStr != null) {
        long adId = Long.parseLong(adIdStr);
        Announcement ad = adsService.getAd(adId);
        request.setAttribute("announcement", ad);
        loadTagsForPage(request, adId);
      }
    } catch (Exception ex) {
      System.err.println("Error loading ad for error page: " + ex.getMessage());
    }
    request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
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

  private void loadTagsForPage(HttpServletRequest request, long adId) {
    try {
      request.setAttribute("availableTags", tagSelector.getTagsWithValues());
      request.setAttribute("currentTags", tagSelector.getTagsForAd(adId));
    } catch (Exception e) {
      System.err.println("Error loading tags for page: " + e.getMessage());
    }
  }

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

  private void handleRemovePhoto(String adIdStr, String photoIndexStr, HttpServletResponse response)
    throws IOException {

    try {
      long adId = Long.parseLong(adIdStr);
      int photoIndex = Integer.parseInt(photoIndexStr);

      // Удаляем фото
      adsService.removePhotoFromAd(adId, photoIndex);
      System.out.println("✅ Photo removed from ad " + adId + " at index " + photoIndex);

      // ВАЖНО: Делаем редирект на GET запрос
      response.sendRedirect("edit-ad?adId=" + adId + "&success=Фотография успешно удалена");

    } catch (Exception e) {
      System.err.println("❌ Error removing photo: " + e.getMessage());
      response.sendRedirect("edit-ad?adId=" + adIdStr + "&error=Ошибка при удалении фотографии");
    }
  }
}