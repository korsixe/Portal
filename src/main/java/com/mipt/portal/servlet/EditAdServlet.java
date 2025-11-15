package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/edit-ad")
public class EditAdServlet extends HttpServlet {

  private AdsService adsService;

  @Override
  public void init() throws ServletException {
    this.adsService = new AdsService();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      String adIdStr = request.getParameter("adId");
      String action = request.getParameter("action");

      if (adIdStr == null) {
        response.sendRedirect("my-ads");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      Announcement announcement = adsService.getAd(adId);

      if (announcement == null) {
        request.setAttribute("error", "Объявление не найдено");
        request.getRequestDispatcher("/my-ads.jsp").forward(request, response);
        return;
      }

      // Обработка быстрых действий
      if ("toDraft".equals(action)) {
        adsService.saveAsDraft(announcement);
        request.setAttribute("success", "Объявление сохранено как черновик");
      }

      request.setAttribute("announcement", announcement);
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);

    } catch (Exception e) {
      request.setAttribute("error", "Ошибка при загрузке объявления: " + e.getMessage());
      request.getRequestDispatcher("/my-ads.jsp").forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      if (!ServletFileUpload.isMultipartContent(request)) {
        processRegularRequest(request, response);
        return;
      }

      DiskFileItemFactory factory = new DiskFileItemFactory();
      factory.setSizeThreshold(1024 * 1024); // 1MB threshold
      factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

      ServletFileUpload upload = new ServletFileUpload(factory);
      upload.setFileSizeMax(1024 * 1024 * 5);
      upload.setSizeMax(1024 * 1024 * 20);

      // Мапы для хранения данных формы
      Map<String, String> formFields = new HashMap<>();
      List<byte[]> photosList = new ArrayList<>();
      String adIdStr = null;

      // Парсим
      List<FileItem> items = upload.parseRequest(request);

      for (FileItem item : items) {
        if (item.isFormField()) {
          // Обычные текстовые
          String fieldName = item.getFieldName();
          String fieldValue = item.getString("UTF-8");
          formFields.put(fieldName, fieldValue);
          if ("adId".equals(fieldName)) {
            adIdStr = fieldValue;
          }
        } else {
          // Файловые
          if ("photos".equals(item.getFieldName()) && item.getSize() > 0) {
            byte[] fileData = item.get();
            photosList.add(fileData);
          }
        }
      }

      if (adIdStr == null) {
        adIdStr = formFields.get("adId");
      }
      
      if (adIdStr == null) {
        response.sendRedirect("my-ads");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      Announcement existingAd = adsService.getAd(adId);

      if (existingAd == null) {
        request.setAttribute("error", "Объявление не найдено");
        request.getRequestDispatcher("/my-ads.jsp").forward(request, response);
        return;
      }

      if (!existingAd.canBeEdited()) {
        request.setAttribute("error", "Это объявление нельзя редактировать в текущем статусе");
        request.setAttribute("announcement", existingAd);
        request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
        return;
      }

      // Получаем новые данные из формы
      String title = formFields.get("title");
      String description = formFields.get("description");
      String categoryStr = formFields.get("category");
      String subcategory = formFields.get("subcategory");
      String conditionStr = formFields.get("condition");
      String location = formFields.get("location");
      String priceType = formFields.get("priceType");
      String priceStr = formFields.get("price");
      String action = formFields.get("action");
      String tags = formFields.get("tags");

      // Обновляем объявление
      existingAd.setTitle(title);
      existingAd.setDescription(description);
      existingAd.setCategory(Category.valueOf(categoryStr));
      existingAd.setSubcategory(subcategory);
      existingAd.setCondition(Condition.valueOf(conditionStr));
      existingAd.setLocation(location);

      // Обрабатываем цену
      int price = processPrice(priceType, priceStr);
      existingAd.setPrice(price);

      // Обрабатываем теги
      if (tags != null && !tags.trim().isEmpty()) {
        List<String> tagList = Arrays.asList(tags.split("\\s*,\\s*"));
        existingAd.setTags(tagList);
      }

      // Обновляем статус
      if ("publish".equals(action)) {
        adsService.activate(existingAd);
      } else {
        adsService.saveAsDraft(existingAd);
      }

      // Сохраняем изменения в БД
      adsService.editAd(existingAd);

      // Обновляем фотографии, если они были загружены
      if (!photosList.isEmpty()) {
        updatePhotos(adId, photosList);
      }

      request.setAttribute("success", "Объявление успешно обновлено!");
      request.setAttribute("announcement", existingAd);
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", "Ошибка при обновлении объявления: " + e.getMessage());
      try {
        String adIdStr = request.getParameter("adId");
        if (adIdStr != null) {
          long adId = Long.parseLong(adIdStr);
          Announcement ad = adsService.getAd(adId);
          if (ad != null) {
            request.setAttribute("announcement", ad);
          }
        }
      } catch (Exception ex) {

        ex.printStackTrace();
      }
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
    }
  }

  private void processRegularRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Обработка обычного запроса (без файлов) для обратной совместимости
    try {
      String adIdStr = request.getParameter("adId");
      if (adIdStr == null) {
        response.sendRedirect("my-ads");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      Announcement existingAd = adsService.getAd(adId);

      if (existingAd == null || !existingAd.canBeEdited()) {
        request.setAttribute("error", "Объявление не найдено или нельзя редактировать");
        request.getRequestDispatcher("/my-ads.jsp").forward(request, response);
        return;
      }

      // Обновляем объявление (без фото)
      existingAd.setTitle(request.getParameter("title"));
      existingAd.setDescription(request.getParameter("description"));
      existingAd.setCategory(Category.valueOf(request.getParameter("category")));
      existingAd.setSubcategory(request.getParameter("subcategory"));
      existingAd.setCondition(Condition.valueOf(request.getParameter("condition")));
      existingAd.setLocation(request.getParameter("location"));

      int price = processPrice(request.getParameter("priceType"), request.getParameter("price"));
      existingAd.setPrice(price);

      String tags = request.getParameter("tags");
      if (tags != null && !tags.trim().isEmpty()) {
        existingAd.setTags(Arrays.asList(tags.split("\\s*,\\s*")));
      }

      String action = request.getParameter("action");
      if ("publish".equals(action)) {
        adsService.activate(existingAd);
      } else {
        adsService.saveAsDraft(existingAd);
      }

      adsService.editAd(existingAd);

      request.setAttribute("success", "Объявление успешно обновлено!");
      request.setAttribute("announcement", existingAd);
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
    } catch (Exception e) {
      request.setAttribute("error", "Ошибка: " + e.getMessage());
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
    }
  }

  private void updatePhotos(long adId, List<byte[]> photosList) throws SQLException {
    try (Connection conn = getConnection()) {
      // Создаем SQL с множественными параметрами для BYTEA[]
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("UPDATE ads SET photos = ARRAY[");
      for (int i = 0; i < photosList.size(); i++) {
        if (i > 0) sqlBuilder.append(", ");
        sqlBuilder.append("?::bytea");
      }
      sqlBuilder.append("] WHERE id = ?");
      
      String updatePhotosSql = sqlBuilder.toString();
      try (PreparedStatement updatePhotosStmt = conn.prepareStatement(updatePhotosSql)) {
        // Устанавливаем каждый bytea параметр
        for (int i = 0; i < photosList.size(); i++) {
          updatePhotosStmt.setBytes(i + 1, photosList.get(i));
        }
        updatePhotosStmt.setLong(photosList.size() + 1, adId);
        int updateResult = updatePhotosStmt.executeUpdate();
        System.out.println("Updated " + photosList.size() + " photos for ad ID: " + adId + " (rows updated: " + updateResult + ")");
      }
    }
  }

  private Connection getConnection() throws SQLException {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new SQLException("PostgreSQL JDBC Driver not found", e);
    }
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/myproject",
        "myuser",
        "mypassword"
    );
  }

  private int processPrice(String priceType, String priceStr) {
    if (priceType == null) {
      return -1;
    }

    switch (priceType) {
      case "fixed":
        try {
          int price = Integer.parseInt(priceStr);
          return price > 0 ? price : -1;
        } catch (NumberFormatException e) {
          return -1;
        }
      case "free":
        return 0;
      case "negotiable":
      default:
        return -1;
    }
  }
}