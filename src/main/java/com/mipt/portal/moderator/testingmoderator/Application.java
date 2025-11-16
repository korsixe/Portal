package com.mipt.portal.moderator.testingmoderator;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.moderator.*;
import com.mipt.portal.users.repository.UserRepository;
import com.mipt.portal.users.repository.UserRepositoryImpl;
import com.mipt.portal.users.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.Scanner;

public class Application {
    private final AdsRepository adsRepository;
    private final ModeratorRepository moderatorRepository;
    private final ModeratorRegistrationImpl moderatorRegistration;
    private final ModeratorLoginImpl moderatorLogin;

    public Application(AdsRepository adsRepository, ModeratorRepository moderatorRepository, ModeratorRegistrationImpl moderatorRegistration, ModeratorLoginImpl moderatorLogin) {
        this.adsRepository = adsRepository;
        this.moderatorRepository = moderatorRepository;
        this.moderatorRegistration = moderatorRegistration;
        this.moderatorLogin = moderatorLogin;
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5433/myproject";
        String username = "myuser";
        String password = "mypassword";

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Драйвер PostgreSQL загружен");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Успешное подключение к БД!");

            ModeratorRepository moderatorRepository = new ModeratorRepository();
            ModeratorRegistrationImpl moderatorRegistration = new ModeratorRegistrationImpl(moderatorRepository);
            ModeratorLoginImpl moderatorLogin = new ModeratorLoginImpl(moderatorRepository);

            runTests(moderatorRepository, moderatorRegistration, moderatorLogin);

            connection.close();
            System.out.println("✅ Соединение с БД закрыто");

        } catch (Exception e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    public static void runTests(ModeratorRepository moderatorRepository, ModeratorRegistrationImpl moderatorRegistration, ModeratorLoginImpl moderatorLogin) {
        testRegistration(moderatorRegistration);
        testLoginAndUpdate(moderatorLogin, moderatorRepository);
        testDeleteAccount(moderatorRepository, moderatorRegistration, moderatorLogin);
    }

    private static void testRegistration(ModeratorRegistrationImpl moderatorRegistration) {
        System.out.println("\n=== TEST 1: РЕГИСТРАЦИЯ В PORTAL ===");

        String email = "test.moderator@phystech.edu";
        String name = "Test Moderator";
        String password = "TestPass123!";

        System.out.println("Тестовые данные:");
        System.out.println("Email: " + email);
        System.out.println("Имя: " + name);
        System.out.println("Пароль: " + password);

        Moderator moderator = new Moderator(email, name, password);
        Optional<Moderator> moderatorOpt = moderatorRegistration.register(moderator);

        if (moderatorOpt.isPresent()) {
            System.out.println("✅ ТЕСТ ПРОЙДЕН: Регистрация успешна!");
            System.out.println("Создан модератор: " + moderatorOpt.get());
        } else {
            System.out.println("❌ ТЕСТ ПРОВАЛЕН: Регистрация не удалась!");
        }
    }

    private static void testLoginAndUpdate(ModeratorLoginImpl moderatorLogin, ModeratorRepository moderatorRepository) {
        System.out.println("\n=== ТЕСТ 2: ВХОД И ИЗМЕНЕНИЕ ДАННЫХ ===");

        String email = "test.moderator@phystech.edu";
        String password = "TestPass123!";
        String newName = "Updated Test Moderator";

        System.out.println("Попытка входа с:");
        System.out.println("Email: " + email);
        System.out.println("Пароль: " + password);

        Moderator moderator = moderatorLogin.login(email, password);

        if (moderator != null) {
            System.out.println("✅ Вход успешен!");
            System.out.println("Текущее имя: " + moderator.getName());

            moderator.setName(newName);
            System.out.println("Новое имя: " + moderator.getName());

            if (moderator.getName().equals(newName)) {
                System.out.println("✅ ТЕСТ ПРОЙДЕН: Данные успешно изменены!");
            } else {
                System.out.println("❌ ТЕСТ ПРОВАЛЕН: Данные не изменились!");
            }
        } else {
            System.out.println("❌ ТЕСТ ПРОВАЛЕН: Вход не удался!");
        }
    }

    private static void testDeleteAccount(ModeratorRepository moderatorRepository,
                                          ModeratorRegistrationImpl moderatorRegistration,
                                          ModeratorLoginImpl moderatorLogin) {
        System.out.println("\n=== ТЕСТ 3: УДАЛЕНИЕ АККАУНТА ===");

        String deleteTestEmail = "delete.test@phystech.edu";
        String deleteTestName = "Delete Test Moderator";
        String deleteTestPassword = "DeletePass123!";

        System.out.println("Создаем тестового модератора для удаления...");
        Moderator deleteModerator = new Moderator(deleteTestEmail, deleteTestName, deleteTestPassword);
        Optional<Moderator> deleteModeratorOpt = moderatorRegistration.register(deleteModerator);

        if (deleteModeratorOpt.isPresent()) {
            System.out.println("✅ Тестовый модератор создан!");
            Moderator createdModerator = deleteModeratorOpt.get();
            Long moderatorId = createdModerator.getId();

            System.out.println("Пытаемся удалить модератора с ID: " + moderatorId);
            boolean deleteResult = moderatorRepository.delete(moderatorId);

            if (deleteResult) {
                System.out.println("✅ Модератор удален из базы данных!");

                Optional<Moderator> foundModerator = moderatorRepository.findByEmail(deleteTestEmail);
                if (foundModerator.isEmpty()) {
                    System.out.println("✅ ТЕСТ ПРОЙДЕН: Аккаунт полностью удален!");
                } else {
                    System.out.println("❌ ТЕСТ ПРОВАЛЕН: Модератор все еще найден в базе!");
                }
            } else {
                System.out.println("❌ ТЕСТ ПРОВАЛЕН: Не удалось удалить модератора из базы!");
            }
        } else {
            System.out.println("❌ ТЕСТ ПРОВАЛЕН: Не удалось создать тестового модератора!");
        }
    }

    private static void updateModeratorInfo(Moderator moderator) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== ИЗМЕНЕНИЕ ДАННЫХ ===");
        System.out.print("Новое имя (текущее: " + moderator.getName() + "): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            System.out.println("✅ Данные успешно обновлены!");
            moderator.setName(newName);
        } else {
            System.out.println("❌ Ошибка при обновлении данных");
        }
    }
}