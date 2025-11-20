package com.mipt.portal.servlet;

import com.mipt.portal.announcement.AdsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

    System.out.println("🚀 AdPhotoServlet: START - " + request.getQueryString());

    String adIdParam = request.getParameter("adId");
    String photoIndexParam = request.getParameter("photoIndex");

    System.out.println("📋 Parameters - adId: " + adIdParam + ", photoIndex: " + photoIndexParam);

    if (adIdParam == null || photoIndexParam == null) {
      System.err.println("❌ Missing parameters");
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters: adId and photoIndex required");
      return;
    }

    try {
      long adId = Long.parseLong(adIdParam);
      int photoIndex = Integer.parseInt(photoIndexParam);

      System.out.println("🔍 Loading photos for adId: " + adId);

      // Получаем все фото для объявления
      List<byte[]> photos = adsService.getAdPhotosBytes(adId);

      System.out.println("📸 Photos loaded: " + (photos != null ? photos.size() : "null"));

      if (photos == null || photos.isEmpty() || photoIndex >= photos.size()) {
        System.err.println("❌ Photo not found - index: " + photoIndex + ", total: " +
          (photos != null ? photos.size() : 0));
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo not found");
        return;
      }

      byte[] photoData = photos.get(photoIndex);

      if (photoData == null || photoData.length == 0) {
        System.err.println("❌ Photo data is empty");
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo data is empty");
        return;
      }

      System.out.println("✅ Photo data size: " + photoData.length + " bytes");

      // Устанавливаем заголовки для правильного отображения изображения
      response.setContentType("image/jpeg");
      response.setContentLength(photoData.length);
      response.setHeader("Cache-Control", "max-age=3600"); // Кэшируем на 1 час
      response.setHeader("Content-Disposition", "inline; filename=\"photo.jpg\"");

      // Записываем данные изображения в ответ
      response.getOutputStream().write(photoData);
      response.getOutputStream().flush();

      System.out.println("✅ Photo sent successfully");

    } catch (NumberFormatException e) {
      System.err.println("❌ Number format error: " + e.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
    } catch (Exception e) {
      System.err.println("❌ Error serving photo: " + e.getMessage());
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading photo");
    }
  }
}