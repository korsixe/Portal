// MarkNotificationReadServlet.java
package com.mipt.portal.moderator.message.servlet;

import com.mipt.portal.notifications.NotificationService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/mark-notification-read")
public class MarkNotificationReadServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("🎯 MarkNotificationReadServlet ИНИЦИАЛИЗИРОВАН по пути: /mark-notification-read");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("🎯 === POST /mark-notification-read ===");
        System.out.println("🎯 RemoteAddr: " + request.getRemoteAddr());
        System.out.println("🎯 Method: " + request.getMethod());

        try {
            String notificationIdParam = request.getParameter("notificationId");
            System.out.println("🎯 notificationId parameter: " + notificationIdParam);

            // Логируем все параметры
            request.getParameterMap().forEach((key, values) -> {
                System.out.println("🎯 Param " + key + ": " + String.join(", ", values));
            });

            if (notificationIdParam == null || notificationIdParam.isEmpty()) {
                System.out.println("❌ notificationId parameter is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Missing notificationId");
                return;
            }

            Long notificationId = Long.parseLong(notificationIdParam);
            System.out.println("🎯 Processing notification ID: " + notificationId);

            NotificationService notificationService = new NotificationService();
            boolean success = notificationService.markAsRead(notificationId);

            System.out.println("🎯 Mark as read result: " + success);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Notification marked as read");
                System.out.println("✅ Successfully marked notification as read");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to mark as read");
                System.out.println("❌ Failed to mark notification as read");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid notificationId format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid notificationId format");
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Server error: " + e.getMessage());
        }

        System.out.println("🎯 === END POST /mark-notification-read ===");
    }
}