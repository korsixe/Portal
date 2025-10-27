package com.mipt.portal.testing_user;

import com.mipt.portal.DatabaseManager;
import com.mipt.portal.service.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.Scanner;

public class Application {
    private final DatabaseManager databaseManager;
    private final UserRegistrationImpl registration;

    public Application(DatabaseManager databaseManager, UserRegistrationImpl registration) {
        this.databaseManager = databaseManager;
        this.registration = registration;
    }

    public static void main(String[] args) {
        // Настройки подключения к БД
        String url = "jdbc:postgresql://localhost:5433/myproject"; // замените на ваши настройки
        String username = "myuser";
        String password = "mypassword";

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Драйвер PostgreSQL загружен");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Успешное подключение к БД!");

            DatabaseManager  databaseManager = new DatabaseManager(connection);
            databaseManager.createTables();
            databaseManager.insertData();

            UserRegistrationImpl registration = new UserRegistrationImpl(databaseManager);

            runRegistrationTest(registration, databaseManager);

            connection.close();
            System.out.println("✅ Соединение с БД закрыто");

        } catch (Exception e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
        }
    }

    public static void runRegistrationTest(UserRegistrationImpl registration, DatabaseManager databaseManager) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== PORTAL REGISTRATION ===\n");
        System.out.print("Введите Вашу физтех-почту: ");
        String email = scanner.nextLine();

        System.out.print("Введите Ваш никнейм: ");
        String name = scanner.nextLine();

        System.out.println("Длина пароля не менее 8 символов, он может содержать: \n" +
                "1) прописные буквы \n" +
                "2) заглавные буквы \n" +
                "3) цифры \n" +
                "4) специальные символы: !?@#$%%&*_-");
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User user = registration.register(email, name, password);

        if (user != null) {
            System.out.println("\n\uD83C\uDF89 Спасибо за регистрацию!");
            System.out.println(user);

            checkDatabase(databaseManager, user.getEmail());
        } else {
            System.out.println("Регистрация не удалась!");
        }

        scanner.close();
    }

    private static void checkDatabase(DatabaseManager databaseManager, String email) {
        System.out.println("\n=== ПРОВЕРКА БАЗЫ ДАННЫХ ===");

        try {
            Thread.sleep(500); // Даем время на коммит

            Optional<User> dbUser = databaseManager.getUserByEmail(email);

            if (dbUser.isPresent()) {
                System.out.println("✅ УСПЕХ: Пользователь найден в БД!");
                User user = dbUser.get();
                System.out.println("ID: " + user.getId());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Имя: " + user.getName());
                System.out.println("Курс: " + user.getCourse());
                System.out.println("Рейтинг: " + user.getRating());
                System.out.println("Коины: " + user.getCoins());
            } else {
                System.out.println("❌ ОШИБКА: Пользователь не найден в БД!");

                // Покажем все email в БД для отладки
                System.out.println("\n=== ВСЕ ПОЧТЫ В БАЗЕ ===");
                var allUsers = databaseManager.getAllUsers();
                if (allUsers.isEmpty()) {
                    System.out.println("В базе нет пользователей");
                } else {
                    for (User u : allUsers) {
                        System.out.println("Email: " + u.getEmail());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при проверке БД: " + e.getMessage());
            e.printStackTrace();
        }
    }
}