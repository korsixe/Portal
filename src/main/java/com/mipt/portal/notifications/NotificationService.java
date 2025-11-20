package com.mipt.portal.notifications;

import com.mipt.portal.moderator.message.ModerationMessage;
import com.mipt.portal.moderator.message.ModerationMessageRepository;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    // Получаем уведомления пользователя с информацией о прочтении
    public List<ModerationMessage> getUserNotifications(Long userId) {
        List<ModerationMessage> notifications = new ArrayList<>();

        try {
            AdsService adsService = new AdsService();
            List<Announcement> userAds = adsService.getUserAds(userId);

            if (userAds.isEmpty()) {
                return notifications;
            }

            ModerationMessageRepository repository = new ModerationMessageRepository();

            for (Announcement ad : userAds) {
                List<ModerationMessage> moderationMessages = repository.getMessagesByAdId(ad.getId());
                notifications.addAll(moderationMessages);
            }
            repository.close();

        } catch (Exception e) {
            System.err.println("❌ Ошибка при загрузке уведомлений: " + e.getMessage());
            e.printStackTrace();
        }

        return notifications;
    }

    // Получаем количество непрочитанных уведомлений
    public int getUnreadCount(Long userId) {
        try {
            ModerationMessageRepository repository = new ModerationMessageRepository();
            int count = repository.getUnreadCountForUser(userId);
            repository.close();
            return count;
        } catch (Exception e) {
            System.err.println("❌ Ошибка при получении количества непрочитанных уведомлений: " + e.getMessage());
            return 0;
        }
    }

    // Пометить уведомление как прочитанное
    public boolean markAsRead(Long notificationId) {
        try {
            System.out.println("🔔 Попытка пометить уведомление " + notificationId + " как прочитанное");
            ModerationMessageRepository repository = new ModerationMessageRepository();
            boolean success = repository.markAsRead(notificationId);
            repository.close();
            System.out.println("🔔 Результат пометки уведомления " + notificationId + ": " + success);
            return success;
        } catch (Exception e) {
            System.err.println("❌ Ошибка при отметке уведомления как прочитанного: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Пометить все уведомления как прочитанные
    public boolean markAllAsRead(Long userId) {
        try {
            ModerationMessageRepository repository = new ModerationMessageRepository();
            boolean success = repository.markAllAsReadForUser(userId);
            repository.close();
            return success;
        } catch (Exception e) {
            System.err.println("❌ Ошибка при отметке всех уведомлений как прочитанных: " + e.getMessage());
            return false;
        }
    }

    // Удалить уведомление
    public boolean deleteNotification(Long notificationId) {
        try {
            ModerationMessageRepository repository = new ModerationMessageRepository();
            boolean success = repository.deleteNotification(notificationId);
            repository.close();
            return success;
        } catch (Exception e) {
            System.err.println("❌ Ошибка при удалении уведомления: " + e.getMessage());
            return false;
        }
    }

    // Остальные методы остаются без изменений
    public String getActionText(String action) {
        switch (action) {
            case "approve": return "одобрено";
            case "reject": return "отправлено на доработку";
            case "delete": return "удалено";
            default: return "обработано";
        }
    }

    public String getActionIcon(String action) {
        switch (action) {
            case "approve": return "✅";
            case "reject": return "⚠️";
            case "delete": return "❌";
            default: return "🔔";
        }
    }

    public String getNotificationTitle(String action, String adTitle) {
        switch (action) {
            case "approve": return "Объявление одобрено";
            case "reject": return "Требуется доработка";
            case "delete": return "Объявление отклонено";
            default: return "Обновление статуса объявления";
        }
    }

    public String getNotificationMessage(String action, String adTitle) {
        switch (action) {
            case "approve": return "Ваше объявление \"" + adTitle + "\" было одобрено модератором";
            case "reject": return "Ваше объявление \"" + adTitle + "\" требует доработки";
            case "delete": return "Ваше объявление \"" + adTitle + "\" было отклонено модератором";
            default: return "Статус вашего объявления \"" + adTitle + "\" был изменен";
        }
    }

    public String getNotificationDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());

        if (duration.toMinutes() < 1) {
            return "только что";
        } else if (duration.toHours() < 1) {
            return duration.toMinutes() + " мин. назад";
        } else if (duration.toDays() < 1) {
            return duration.toHours() + " ч. назад";
        } else {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return dateTime.format(formatter);
        }
    }
}