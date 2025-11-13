package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
      String adIdStr = request.getParameter("adId");
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
        adsService.sendToModeration(existingAd);
      } else {
        adsService.saveAsDraft(existingAd);
      }

      // Сохраняем изменения в БД
      adsService.editAd(existingAd);

      request.setAttribute("success", "Объявление успешно обновлено!");
      request.setAttribute("announcement", existingAd);
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);

    } catch (Exception e) {
      request.setAttribute("error", "Ошибка при обновлении объявления: " + e.getMessage());
      try {
        String adIdStr = request.getParameter("adId");
        if (adIdStr != null) {
          long adId = Long.parseLong(adIdStr);
          Announcement ad = adsService.getAd(adId);
          request.setAttribute("announcement", ad);
        }
      } catch (Exception ex) {
        // Игнорируем ошибку загрузки объявления
      }
      request.getRequestDispatcher("/edit-ad.jsp").forward(request, response);
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