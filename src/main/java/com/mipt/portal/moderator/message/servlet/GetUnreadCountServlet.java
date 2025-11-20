package com.mipt.portal.moderator.message.servlet;

import com.mipt.portal.notifications.NotificationService;
import com.mipt.portal.users.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/get-unread-count")
public class GetUnreadCountServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            System.out.println("🔔 Получен запрос на количество непрочитанных для пользователя: " + (user != null ? user.getId() : "null"));

            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            NotificationService notificationService = new NotificationService();
            int unreadCount = notificationService.getUnreadCount(user.getId());

            System.out.println("✅ Количество непрочитанных: " + unreadCount);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"unreadCount\": " + unreadCount + "}");
        } catch (Exception e) {
            System.err.println("❌ Ошибка в GetUnreadCountServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}