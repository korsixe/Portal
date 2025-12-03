package com.mipt.portal.servlet;

import com.mipt.portal.announcementContent.MediaManager;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;

import static com.mipt.portal.database.DatabaseConnection.getConnection;

@WebServlet("/delete-photo")
public class DeletePhotoServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    out.println("<html><body>");
    out.println("<h1>DeletePhotoServlet is WORKING!</h1>");
    out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
    out.println("<p>Context Path: " + request.getContextPath() + "</p>");
    out.println("<p>Full URL: " + request.getRequestURL() + "</p>");

    // Форма для тестирования POST
    out.println("<h3>Test Photo Delete:</h3>");
    out.println("<form method='POST'>");
    out.println("adId: <input type='number' name='adId' value='7'><br>");
    out.println("photoIndex: <input type='number' name='photoIndex' value='0'><br>");
    out.println("<button type='submit'>Delete Photo (POST)</button>");
    out.println("</form>");

    out.println("</body></html>");

    System.out.println("✅ GET request to DeletePhotoServlet");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();

    try {
      String adIdParam = request.getParameter("adId");
      String photoIndexParam = request.getParameter("photoIndex");

      if (adIdParam == null || photoIndexParam == null) {
        response.setStatus(400);
        out.write("error: Missing parameters");
        return;
      }

      int adId = Integer.parseInt(adIdParam);
      int photoIndex = Integer.parseInt(photoIndexParam);

      // Получаем соединение
      try (Connection conn = getConnection()) {
        // Используем MediaManager
        try (MediaManager mediaManager = new MediaManager(conn, adId)) {

          // Загружаем текущие фото
          mediaManager.loadFromDB();

          // Проверяем, существует ли фото
          int photoCount = mediaManager.getPhotosCount();
          if (photoIndex < 0 || photoIndex >= photoCount) {
            response.setStatus(400);
            out.write("error: Invalid photo index");
            return;
          }

          // Удаляем фото
          mediaManager.deleteFromDB(photoIndex);

          // Успех
          response.setStatus(200);
          out.write("success");

          System.out.println("✅ Photo deleted: adId=" + adId + ", index=" + photoIndex);
        }
      }

    } catch (NumberFormatException e) {
      response.setStatus(400);
      out.write("error: Invalid parameter format");
    } catch (SQLException e) {
      response.setStatus(500);
      out.write("error: Database error: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      response.setStatus(500);
      out.write("error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
