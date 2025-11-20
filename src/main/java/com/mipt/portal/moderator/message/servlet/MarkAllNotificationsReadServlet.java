package com.mipt.portal.moderator.message.servlet;

import com.mipt.portal.notifications.NotificationService;
import com.mipt.portal.users.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/mark-all-notifications-read")
public class MarkAllNotificationsReadServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("✅ MarkAllNotificationsReadServlet инициализирован по пути: /mark-all-notifications-read");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            System.out.println("📧 Получен запрос на пометку всех прочитанными для пользователя: " + (user != null ? user.getId() : "null"));

            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            NotificationService notificationService = new NotificationService();
            boolean success = notificationService.markAllAsRead(user.getId());

            System.out.println("✅ Результат пометки всех прочитанными: " + success);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка в MarkAllNotificationsReadServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}