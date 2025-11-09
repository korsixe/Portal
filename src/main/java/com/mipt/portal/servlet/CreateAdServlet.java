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

@WebServlet("/create-ad")
public class CreateAdServlet extends HttpServlet {
  private AdsService adsService;

  @Override
  public void init() throws ServletException {
    try {
      // Инициализируем AdsRepository и AdsService
      AdsRepository adsRepository = new AdsRepository(); // Убедись что у AdsRepository есть конструктор без параметров
      this.adsService = new AdsService(adsRepository);
    } catch (Exception e) {
      throw new ServletException("Error initializing AdsService", e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Проверяем авторизацию
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("userId") == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    request.setAttribute("categories", Category.values());
    request.setAttribute("conditions", Condition.values());
    request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("userId") == null) {
      response.sendRedirect("login.jsp");
      return;
    }

    long userId = (Long) session.getAttribute("userId");

    try {
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
        request.setAttribute("error", "Все обязательные поля должны быть заполнены");
        request.setAttribute("categories", Category.values());
        request.setAttribute("conditions", Condition.values());
        request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
        return;
      }

      Category category = Category.valueOf(categoryStr);
      Condition condition = Condition.valueOf(conditionStr);
      int price = parsePrice(priceType, priceValue);

      // Создаем объявление используя твой существующий сервис
      Announcement ad = createAdFromForm(userId, title, description, category,
          condition, price, location, action);

      response.sendRedirect("index.jsp?success=true&adId=" + ad.getId());

    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute("error", "Ошибка при создании объявления: " + e.getMessage());
      request.setAttribute("categories", Category.values());
      request.setAttribute("conditions", Condition.values());
      request.getRequestDispatcher("/create-ad.jsp").forward(request, response);
    }
  }

  private Announcement createAdFromForm(long userId, String title, String description,
      Category category, Condition condition,
      int price, String location, String action)
      throws SQLException {

    // Создаем объявление как в твоем консольном приложении
    Announcement ad = new Announcement(title, description, category, condition,
        price, location, userId);

    if ("publish".equals(action)) {
      ad.sendToModeration();
    }
    // Если "draft" - оставляем как черновик

    // Сохраняем через репозиторий
    long adId = adsService.getAdsRepository().saveAd(ad);
    ad.setId(adId);

    return ad;
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
        return (priceValue != null && !priceValue.isEmpty()) ?
            Integer.parseInt(priceValue) : 0;
      default:
        return -1;
    }
  }
}