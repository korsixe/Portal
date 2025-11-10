package com.mipt.portal.servlet;

import com.mipt.portal.announcement.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/create-ad")
public class CreateAdServlet extends HttpServlet {

  private AdsService adsService;
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

    System.out.println("📥 GET запрос на /create-ad");

    // Временное решение для тестирования
    HttpSession session = request.getSession(true);
    try {
      System.out.println("👤 Ищем пользователя по email...");
      Long testUserId = adsService.getUserIdByEmail("shabunina.ao@phystech.edu");

      if (testUserId != null) {
        session.setAttribute("userId", testUserId);
        System.out.println("✅ Установлен test user ID: " + testUserId);
      } else {
        System.out.println("⚠️  Test user не найден, используем fallback");
        session.setAttribute("userId", 1L);
      }
    } catch (SQLException e) {
      System.err.println("❌ Ошибка получения пользователя: " + e.getMessage());
      session.setAttribute("userId", 1L);
    }

    request.setAttribute("categories", Category.values());
    request.setAttribute("conditions", Condition.values());
    request.getRequestDispatcher("/create-ad.jsp").forward(request, response);

    System.out.println("✅ GET запрос обработан");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    System.out.println("📤 POST запрос на /create-ad");

    // Уберем авторизацию на данный момент
    /*
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("userId") == null) {
      System.out.println("❌ Пользователь не авторизован");
      response.sendRedirect("login.jsp");
      return;
    }*/


    try {
      Long userId = adsService.getUserIdByEmail("shabunina.ao@phystech.edu");

      String title = request.getParameter("title");
      String description = request.getParameter("description");
      String categoryStr = request.getParameter("category");
      String location = request.getParameter("location");
      String conditionStr = request.getParameter("condition");
      String priceType = request.getParameter("priceType");
      String priceValue = request.getParameter("price");
      String action = request.getParameter("action");

      // Валидация обязательных полей
      if (title == null || title.trim().isEmpty() ||
          description == null || description.trim().isEmpty() ||
          categoryStr == null || conditionStr == null) {
        System.out.println("❌ Не все обязательные поля заполнены");
        request.setAttribute("error", "Все обязательные поля должны быть заполнены");
        forwardToForm(request, response);
        return;
      }

      // Валидация категории и состояния
      Category category;
      Condition condition;
      try {
        category = Category.valueOf(categoryStr);
        condition = Condition.valueOf(conditionStr);
        System.out.println("✅ Категория и состояние валидны");
      } catch (IllegalArgumentException e) {
        System.out.println("❌ Неверное значение категории или состояния");
        request.setAttribute("error", "Неверное значение категории или состояния");
        forwardToForm(request, response);
        return;
      }

      int price = parsePrice(priceType, priceValue);
      System.out.println("🛠️  Создаем объявление...");
      Announcement ad = adsService.createAd(userId, title, description, category,
          condition, price, location, action);

      System.out.println("✅ Объявление создано с ID: " + ad.getId());
      response.sendRedirect("index.jsp?success=true&adId=" + ad.getId());

    } catch (ServletException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private int parsePrice(String priceType, String priceValue) {
    if (priceType == null) {
      return -1; // договорная по умолчанию
    }

    switch (priceType) {
      case "negotiable":
        return -1;
      case "free":
        return 0;
      case "fixed":
        if (priceValue != null && !priceValue.isEmpty()) {
          try {
            return Integer.parseInt(priceValue);
          } catch (NumberFormatException e) {
            return 0;
          }
        }
        return 0;
      default:
        return -1;
    }
  }

  private void forwardToForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setAttribute("categories", Category.values());
    request.setAttribute("conditions", Condition.values());
    request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
  }
}