package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;

import com.mipt.portal.users.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/confirm-delete")
public class ConfirmDeleteServlet extends HttpServlet {

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

      if (adIdStr == null) {
        response.sendRedirect("dashboard.jsp");
        return;
      }

      long adId = Long.parseLong(adIdStr);

      // Получаем объявление для проверки прав доступа
      Announcement ad = adsService.getAd(adId);
      if (ad == null) {
        request.setAttribute("error", "Объявление не найдено");
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        return;
      }

      // Проверяем, что пользователь имеет право удалять это объявление
      HttpSession session = request.getSession();
      User user = (User) session.getAttribute("user");
      if (user == null || user.getId() != ad.getUserId()) {
        request.setAttribute("error", "У вас нет прав для удаления этого объявления");
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        return;
      }

      request.setAttribute("ad", ad);
      request.getRequestDispatcher("/confirm-delete.jsp").forward(request, response);

    } catch (Exception e) {
      request.setAttribute("error", "Ошибка при загрузке объявления: " + e.getMessage());
      request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      String adIdStr = request.getParameter("adId");
      String confirm = request.getParameter("confirm");

      if (adIdStr == null || !"yes".equals(confirm)) {
        response.sendRedirect("dashboard.jsp");
        return;
      }

      long adId = Long.parseLong(adIdStr);
      adsService.hardDeleteAd(adId);

      HttpSession session = request.getSession();
      session.setAttribute("success", "Объявление успешно удалено");
      response.sendRedirect("dashboard.jsp");


    } catch (Exception e) {
      request.setAttribute("error", "Ошибка при удалении объявления: " + e.getMessage());
      request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
  }
}