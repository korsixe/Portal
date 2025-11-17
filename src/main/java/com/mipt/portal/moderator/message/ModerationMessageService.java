package com.mipt.portal.moderator.message;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.AdsService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModerationMessageService {

    static AdsService adsService = new AdsService();

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logModerationAction(Long adId, String action, String reason, String moderatorEmail) {
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("⚠️ Причина не указана, сообщение модератора не сохраняется");
            return;
        }

        String timestamp = LocalDateTime.now().format(formatter);

        System.out.println("📝 У объявления с Id " + adId + " обновлён статус: " + action + " по причине: " + reason + ". Модератор: " + moderatorEmail);

        ModerationMessageRepository repository = null;
        try {
            repository = new ModerationMessageRepository();

            // Проверяем существование таблицы
            boolean tableExists = repository.checkTableExists();
            if (!tableExists) {
                System.err.println("❌ Таблица moderation_messages не существует!");
                return;
            }

            System.out.println("✅ Таблица moderation_messages существует, сохраняем сообщение...");

            Long idMessage = repository.saveModerationMessage(adId, moderatorEmail, action, reason);

            if (idMessage != null) {
                System.out.println("✅ Сообщение модератора успешно сохранено в базу данных");
                adsService.addCommentModerator(adId, idMessage);
            } else {
                System.out.println("❌ Ошибка: сообщение модератора не было сохранено");
            }
        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка при сохранении сообщения модератора: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (repository != null) {
                repository.close();
            }
        }
    }

    /**
     * Для отладки: полная проверка базы данных
     */
    public static void testDatabase() {
        ModerationMessageRepository repository = null;
        try {
            repository = new ModerationMessageRepository();

            // Проверяем соединение
            System.out.println("🔍 Проверка базы данных...");

            // Проверяем существование таблицы
            boolean tableExists = repository.checkTableExists();
            if (tableExists) {
                System.out.println("✅ Таблица moderation_messages существует");

                // Пробуем выполнить простой запрос
                var messages = repository.getMessagesByAdId(1L);
                System.out.println("✅ Запрос к таблице выполняется успешно");

                // Показываем количество сообщений
                var allMessages = repository.getMessagesByModerator("test");
                System.out.println("✅ Всего сообщений модераторов в базе: " + allMessages.size());

            } else {
                System.err.println("❌ Таблица moderation_messages не существует!");
                System.err.println("💡 Убедитесь, что SQL скрипт выполнился успешно");
            }

        } catch (Exception e) {
            System.err.println("❌ Ошибка при работе с базой данных: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (repository != null) {
                repository.close();
            }
        }
    }
}