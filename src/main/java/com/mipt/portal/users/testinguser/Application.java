//package com.mipt.portal.users.testinguser;
//
//import com.mipt.portal.announcement.AdsRepository;
//import com.mipt.portal.users.repository.UserRepository;
//import com.mipt.portal.users.repository.UserRepositoryImpl;
//import com.mipt.portal.users.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.Optional;
//import java.util.Scanner;
//
//public class Application {
//    private final AdsRepository databaseManager;
//    private final UserRepository userRepository;
//    private final UserRegistrationImpl registration;
//    private final UserLoginImpl login;
//
//    public Application(AdsRepository databaseManager, UserRepository userRepository, UserRegistrationImpl registration, UserLoginImpl login) {
//        this.databaseManager = databaseManager;
//        this.userRepository = userRepository;
//        this.registration = registration;
//        this.login = login;
//    }
//
//    public static void main(String[] args) {
//
//        String url = "jdbc:postgresql://localhost:5433/myproject";
//        String username = "myuser";
//        String password = "mypassword";
//
//        try {
//            Class.forName("org.postgresql.Driver");
//            System.out.println("✅ Драйвер PostgreSQL загружен");
//            Connection connection = DriverManager.getConnection(url, username, password);
//            System.out.println("✅ Успешное подключение к БД!");
//
//            AdsRepository databaseManager = new AdsRepository(connection);
//            databaseManager.createTables();
//            databaseManager.insertData();
//
//
//            testUserService();
//
//            connection.close();
//            System.out.println("✅ Соединение с БД закрыто");
//
//        } catch (Exception e) {
//            System.err.println("Ошибка подключения к БД: " + e.getMessage());
//        }
//    }
//
//    public static void runApplication(UserRegistrationImpl registration,UserLoginImpl login, UserRepository userRepository) {
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("\n=== PORTAL SYSTEM ===");
//            System.out.println("1. Регистрация");
//            System.out.println("2. Вход");
//            System.out.println("3. Выход");
//            System.out.print("Выберите действие: ");
//
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//
//            switch (choice) {
//                case 1:
//                    testRegistration(registration, userRepository, scanner);
//                    System.out.println();
//                    break;
//                case 2:
//                    testLogin(login, userRepository, scanner);
//                    System.out.println();
//                    break;
//                case 3:
//                    System.out.println("До свидания!");
//                    System.out.println();
//                    scanner.close();
//                    return;
//                default:
//                    System.out.println("Введите число (1-3)");
//            }
//        }
//    }
//
//    private static void testRegistration(UserRegistrationImpl registration, UserRepository userRepository, Scanner scanner) {
//        System.out.println("\n=== РЕГИСТРАЦИЯ В PORTAL ===");
//
//        System.out.print("Введите Вашу физтех-почту: ");
//        String email = scanner.nextLine();
//
//        System.out.print("Введите Ваш никнейм: ");
//        String name = scanner.nextLine();
//
//        System.out.println("Длина пароля не менее 8 символов, он может содержать: \n" +
//                "1) прописные буквы \n" +
//                "2) заглавные буквы \n" +
//                "3) цифры \n" +
//                "4) специальные символы: !?@#$%&*_-");
//        System.out.print("Введите пароль: ");
//        String password = scanner.nextLine();
//
//        User user = registration.register(email, name, password, password);
//
//        if (user != null) {
//            System.out.println("\n\uD83C\uDF89 Спасибо за регистрацию!");
//            System.out.println(user);
//        } else {
//            System.out.println("Регистрация не удалась!");
//        }
//    }
//
//    private static void testLogin(UserLoginImpl login, UserRepository userRepository, Scanner scanner) {
//        System.out.println("\n=== ВХОД В СИСТЕМУ ===");
//        System.out.print("Введите email: ");
//        String email = scanner.nextLine();
//
//        System.out.print("Введите пароль: ");
//        String password = scanner.nextLine();
//
//        User user = login.login(email, password);
//
//        if (user != null) {
//            System.out.println("Вход выполнен успешно!\nЗдравствуйте, " + user.getName());
//            testUserMenu(user, userRepository, scanner);
//        }
//    }
//
//    private static void testUserMenu(User user, UserRepository userRepository, Scanner scanner) {
//        while (true) {
//            System.out.println("\n=== ЛИЧНЫЙ КАБИНЕТ (" + user.getName() + ") ===");
//            System.out.println("1. Посмотреть данные");
//            System.out.println("2. Изменить данные");
//            System.out.println("3. Выйти из аккаунта");
//            System.out.println("4. Удалить аккаунт");
//            System.out.print("Выберите действие: ");
//
//            int choice = scanner.nextInt();
//            scanner.nextLine(); // consume newline
//
//            switch (choice) {
//                case 1:
//                    System.out.println(user);
//                    break;
//                case 2:
//                    updateUserInfo(user, userRepository, scanner);
//                    break;
//                case 3:
//                    System.out.println("До свидания, " + user.getName() + "!");
//                    return;
//                case 4:
//                    if (deleteAccount(user, userRepository, scanner)) {
//                        return;
//                    }
//                    break;
//                default:
//                    System.out.println("Неверный выбор");
//            }
//        }
//    }
//
//    private static void updateUserInfo(User user, UserRepository userRepository, Scanner scanner) {
//        System.out.println("\n=== ИЗМЕНЕНИЕ ДАННЫХ ===");
//        System.out.print("Новое имя (текущее: " + user.getName() + "): ");
//        String newName = scanner.nextLine().trim();
//        if (!newName.isEmpty()) {
//            user.setName(newName);
//        }
//
//        System.out.print("Новый адрес (текущий: " + user.getAddress() + "): ");
//        String newAddress = scanner.nextLine().trim();
//        if (!newAddress.isEmpty()) {
//            user.setAddress(newAddress);
//        }
//
//        System.out.print("Новая программа обучения (текущая: " + user.getStudyProgram() + "): ");
//        String newProgram = scanner.nextLine().trim();
//        if (!newProgram.isEmpty()) {
//            user.setStudyProgram(newProgram);
//        }
//
//        System.out.print("Новый курс (текущий: " + user.getCourse() + "): ");
//        String newCourse = scanner.nextLine().trim();
//        if (!newCourse.isEmpty()) {
//            user.setCourse(Integer.parseInt(newCourse));
//        }
//
//        if (userRepository.update(user)) {
//            System.out.println("✅ Данные успешно обновлены!");
//        } else {
//            System.out.println("❌ Ошибка при обновлении данных");
//        }
//    }
//
//    private static boolean deleteAccount(User user, UserRepository userRepository, Scanner scanner) {
//        System.out.println("\nВы уверены что хотите удалить ваш аккаунт? (Y/N)");
//        String answer = scanner.nextLine().toUpperCase();
//
//        if (answer.equals("Y") || answer.equals("YES") || answer.equals("ДА")) {
//            System.out.println("ВНИМАНИЕ: Ваши данные будут безвозвратно удалены!");
//            System.out.println("\nДля подтверждения введите пароль: ");
//            String password = scanner.nextLine();
//
//            if (!user.getPassword().equals(password)) {
//                System.out.println("❌ Неверный пароль! Удаление отменено.");
//                return false;
//            }
//
//            if (userRepository.delete(user.getId())) {
//                System.out.println("Аккаунт удален! :(");
//                return true;
//            } else {
//                System.out.println("Ошибка при удалении аккаунта.");
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    public static void testUserService() {
//        UserServiceTest testDemo = new UserServiceTest();
//
//        try {
//            System.out.println("=== ТЕСТИРОВАНИЕ USER SERVICE ===\n");
//
//            testDemo.testCompleteUserLifecycle();
//
//            System.out.println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===");
//
//        } catch (Exception e) {
//            System.err.println("❌ Ошибка при тестировании: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}