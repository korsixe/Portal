package com.mipt.portal.moderator.message.servlet;

import com.mipt.portal.notifications.NotificationService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/delete-notification")
public class DeleteNotificationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String notificationIdParam = request.getParameter("notificationId");
            System.out.println("🗑️ Получен запрос на удаление уведомления: " + notificationIdParam);

            if (notificationIdParam == null || notificationIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Long notificationId = Long.parseLong(notificationIdParam);
            NotificationService notificationService = new NotificationService();
            boolean success = notificationService.deleteNotification(notificationId);

            System.out.println("✅ Результат удаления: " + success);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка в DeleteNotificationServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}