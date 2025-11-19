package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ad-photo")
public class AdPhotoServlet extends HttpServlet {

  private AdsService adsService;

  @Override
  public void init() throws ServletException {
    this.adsService = new AdsService();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String adIdParam = request.getParameter("adId");
    String photoIndexParam = request.getParameter("photoIndex");

    if (adIdParam == null || photoIndexParam == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters: adId and photoIndex required");
      return;
    }

    try {
      long adId = Long.parseLong(adIdParam);
      int photoIndex = Integer.parseInt(photoIndexParam);

      // Получаем фото из базы данных
      var photos = adsService.getAdPhotosBytes(adId);

      if (photos != null && photoIndex < photos.size()) {
        byte[] photoData = photos.get(photoIndex);

        // Определяем Content-Type на основе сигнатуры изображения
        String contentType = determineContentType(photoData);

        // Устанавливаем заголовки
        response.setContentType(contentType);
        response.setContentLength(photoData.length);
        response.setHeader("Cache-Control", "max-age=3600"); // Кэшируем на 1 час

        // Отправляем фото
        response.getOutputStream().write(photoData);

        System.out.println("✅ Sent photo " + photoIndex + " for ad " + adId + " (" + photoData.length + " bytes, " + contentType + ")");
      } else {
        System.err.println("❌ Photo not found: adId=" + adId + ", index=" + photoIndex + ", available=" + (photos != null ? photos.size() : 0));
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo not found");
      }

    } catch (NumberFormatException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters: adId and photoIndex must be numbers");
    } catch (SQLException e) {
      System.err.println("❌ Database error: " + e.getMessage());
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
    } catch (Exception e) {
      System.err.println("❌ Unexpected error: " + e.getMessage());
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
    }
  }

  // 🔥 Определяем Content-Type по сигнатуре изображения
  private String determineContentType(byte[] data) {
    if (data == null || data.length < 4) return "image/jpeg"; // fallback

    // JPEG: FF D8 FF
    if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
      return "image/jpeg";
    }
    // PNG: 89 50 4E 47
    if ((data[0] & 0xFF) == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47) {
      return "image/png";
    }
    // GIF: 47 49 46 38
    if (data[0] == 0x47 && data[1] == 0x49 && data[2] == 0x46 && data[3] == 0x38) {
      return "image/gif";
    }

    return "image/jpeg"; // fallback to JPEG
  }
}