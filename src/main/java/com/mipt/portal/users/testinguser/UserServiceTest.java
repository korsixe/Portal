//package com.mipt.portal.users.testinguser;
//
//import com.mipt.portal.users.User;
//import com.mipt.portal.users.service.UserService;
//
//import java.sql.SQLException;
//import java.util.Optional;
//
//public class UserServiceTest {
//
//    private final UserService userService;
//
//    public UserServiceTest() {
//        this.userService = new UserService();
//    }
//
//    public static void main(String[] args) {
//        UserServiceTest frontendTest = new UserServiceTest();
//
//        try {
//            System.out.println("=== ТЕСТИРОВАНИЕ USER SERVICE ===\n");
//
//            frontendTest.testCompleteUserLifecycle();
//
//            System.out.println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===");
//
//        } catch (Exception e) {
//            System.err.println("❌ Ошибка при тестировании: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private static class RegistrationRequest {
//        String email;
//        String name;
//        String password;
//        String address;
//        String studyProgram;
//        int course;
//
//        public RegistrationRequest(String email, String name, String password, String address, String studyProgram, int course) {
//            this.email = email;
//            this.name = name;
//            this.password = password;
//            this.address = address;
//            this.studyProgram = studyProgram;
//            this.course = course;
//        }
//    }
//
//    private static class UpdateUserRequest {
//        Long userId;
//        String name;
//        String address;
//        String studyProgram;
//        int course;
//        double rating;
//
//        public UpdateUserRequest(Long userId, String name, String address, String studyProgram, int course, double rating) {
//            this.userId = userId;
//            this.name = name;
//            this.address = address;
//            this.studyProgram = studyProgram;
//            this.course = course;
//            this.rating = rating;
//        }
//    }
//
//    private static class AddCoinsRequest {
//        Long userId;
//        int coins;
//
//        public AddCoinsRequest(Long userId, int coins) {
//            this.userId = userId;
//            this.coins = coins;
//        }
//    }
//
//    // Тестовые данные
//    private static class TestData {
//        static final RegistrationRequest REGISTRATION = new RegistrationRequest(
//                "frontend.user@phystech.edu",
//                "Фронтенд Пользователь",
//                "FrontendPass123!",
//                "Москва, ул. Фронтендовая, 456",
//                "Информатика и вычислительная техника",
//                3
//        );
//
//        static final UpdateUserRequest UPDATE = new UpdateUserRequest(
//                null, // будет установлен после регистрации
//                "Обновленный Фронтенд Пользователь",
//                "Новый адрес, ул. Обновленная, 789",
//                "Программная инженерия",
//                4,
//                4.7
//        );
//
//        static final AddCoinsRequest ADD_COINS = new AddCoinsRequest(null, 300);
//        static final AddCoinsRequest DEDUCT_COINS = new AddCoinsRequest(null, 150);
//    }
//
//    void testCompleteUserLifecycle() throws SQLException {
//        System.out.println("=== ПОЛНЫЙ ЦИКЛ РАБОТЫ С ПОЛЬЗОВАТЕЛЕМ ===\n");
//
//        Long userId = null;
//
//        try {
//            System.out.println("1. 📝 РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ");
//            User registeredUser = userService.registerUser(
//                    TestData.REGISTRATION.email,
//                    TestData.REGISTRATION.name,
//                    TestData.REGISTRATION.password,
//                    TestData.REGISTRATION.password,
//                    TestData.REGISTRATION.address,
//                    TestData.REGISTRATION.studyProgram,
//                    TestData.REGISTRATION.course
//            );
//
//            userId = registeredUser.getId();
//            TestData.UPDATE.userId = userId;
//            TestData.ADD_COINS.userId = userId;
//            TestData.DEDUCT_COINS.userId = userId;
//
//            System.out.println("✅ Пользователь зарегистрирован:");
//            printUserInfo(registeredUser);
//
//            System.out.println("\n2. 🔍 ПОИСК ПОЛЬЗОВАТЕЛЯ ПО EMAIL");
//            Optional<User> foundByEmail = userService.findUserByEmail(TestData.REGISTRATION.email);
//            if (foundByEmail.isPresent()) {
//                System.out.println("✅ Пользователь найден по email");
//            } else {
//                System.out.println("❌ Пользователь не найден по email");
//                return;
//            }
//
//            System.out.println("\n3. 🔍 ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID");
//            Optional<User> foundById = userService.findUserById(userId);
//            if (foundById.isPresent()) {
//                System.out.println("✅ Пользователь найден по ID");
//            } else {
//                System.out.println("❌ Пользователь не найден по ID");
//                return;
//            }
//
//            System.out.println("\n4. ✏️ ОБНОВЛЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ");
//            User userToUpdate = foundById.get();
//            userToUpdate.setName(TestData.UPDATE.name);
//            userToUpdate.setAddress(TestData.UPDATE.address);
//            userToUpdate.setStudyProgram(TestData.UPDATE.studyProgram);
//            userToUpdate.setCourse(TestData.UPDATE.course);
//            userToUpdate.setRating(TestData.UPDATE.rating);
//
//            Optional<User> updatedUser = userService.updateUser(userToUpdate);
//            if (updatedUser.isPresent()) {
//                System.out.println("✅ Данные пользователя обновлены:");
//                printUserInfo(updatedUser.get());
//            } else {
//                System.out.println("❌ Не удалось обновить данные пользователя");
//                return;
//            }
//
//            System.out.println("\n5. 💰 ОПЕРАЦИИ С КОИНАМИ");
//
//            userService.updateUserCoins(userId, 500);
//            System.out.println("✅ Установлены начальные коины: 500");
//
//            userService.addCoins(userId, TestData.ADD_COINS.coins);
//            System.out.println("✅ Добавлены коины: +" + TestData.ADD_COINS.coins);
//
//            userService.deductCoins(userId, TestData.DEDUCT_COINS.coins);
//            System.out.println("✅ Списаны коины: -" + TestData.DEDUCT_COINS.coins);
//
//            Optional<User> userWithCoins = userService.findUserById(userId);
//            if (userWithCoins.isPresent()) {
//                System.out.println("📊 Текущий баланс коинов: " + userWithCoins.get().getCoins());
//            }
//
//            System.out.println("\n6. ⭐ ОБНОВЛЕНИЕ РЕЙТИНГА");
//            boolean ratingUpdated = userService.updateUserRating(userId, 4.9);
//            if (ratingUpdated) {
//                System.out.println("✅ Рейтинг обновлен: 4.9");
//            }
//
//            System.out.println("\n7. 📊 ПОЛУЧЕНИЕ СПИСКА ВСЕХ ПОЛЬЗОВАТЕЛЕЙ");
//            var allUsers = userService.getAllUsers();
//            System.out.println("✅ Всего пользователей в системе: " + allUsers.size());
//
//            System.out.println("\n8. ✅ ПРОВЕРКА СУЩЕСТВОВАНИЯ EMAIL");
//            boolean emailExists = userService.existsByEmail(TestData.REGISTRATION.email);
//            System.out.println("Email '" + TestData.REGISTRATION.email + "' существует: " + emailExists);
//
//            System.out.println("\n9. 🧪 ТЕСТ ОШИБОЧНЫХ СЦЕНАРИЕВ");
//
//            try {
//                userService.deductCoins(userId, 10000);
//            } catch (IllegalArgumentException e) {
//                System.out.println("✅ Корректная обработка недостатка коинов: " + e.getMessage());
//            }
//
//            try {
//                userService.updateUserRating(userId, 6.0);
//            } catch (IllegalArgumentException e) {
//                System.out.println("✅ Корректная обработка невалидного рейтинга: " + e.getMessage());
//            }
//
//            System.out.println("\n10. 📋 ФИНАЛЬНЫЕ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ");
//            Optional<User> finalUser = userService.findUserById(userId);
//            if (finalUser.isPresent()) {
//                System.out.println("✅ Финальные данные пользователя:");
//                printUserInfo(finalUser.get());
//            }
//
//        } finally {
//            if (userId != null) {
//                System.out.println("\n11. 🗑️ ОЧИСТКА - УДАЛЕНИЕ ТЕСТОВОГО ПОЛЬЗОВАТЕЛЯ");
//                boolean deleted = userService.deleteUser(userId);
//                if (deleted) {
//                    System.out.println("✅ Тестовый пользователь удален");
//                } else {
//                    System.out.println("❌ Не удалось удалить тестового пользователя");
//                }
//            }
//        }
//    }
//
//    private void printUserInfo(User user) {
//        System.out.println("   ID: " + user.getId());
//        System.out.println("   Email: " + user.getEmail());
//        System.out.println("   Имя: " + user.getName());
//        System.out.println("   Адрес: " + user.getAddress());
//        System.out.println("   Учебная программа: " + user.getStudyProgram());
//        System.out.println("   Курс: " + user.getCourse());
//        System.out.println("   Рейтинг: " + user.getRating());
//        System.out.println("   Коины: " + user.getCoins());
//    }
//
//    public void testMultipleUsers() throws SQLException {
//        System.out.println("\n=== ТЕСТ НЕСКОЛЬКИХ ПОЛЬЗОВАТЕЛЕЙ ===");
//
//        RegistrationRequest[] users = {
//                new RegistrationRequest("user1@test.com", "User One", "pass1", "Addr1", "Prog1", 1),
//                new RegistrationRequest("user2@test.com", "User Two", "pass2", "Addr2", "Prog2", 2),
//                new RegistrationRequest("user3@test.com", "User Three", "pass3", "Addr3", "Prog3", 3)
//        };
//
//        Long[] userIds = new Long[users.length];
//
//        try {
//            for (int i = 0; i < users.length; i++) {
//                User user = userService.registerUser(
//                        users[i].email,
//                        users[i].name,
//                        users[i].password,
//                        users[i].password,
//                        users[i].address,
//                        users[i].studyProgram,
//                        users[i].course
//                );
//                userIds[i] = user.getId();
//                System.out.println("✅ Зарегистрирован пользователь: " + user.getName());
//            }
//
//            var allUsers = userService.getAllUsers();
//            System.out.println("Всего пользователей в системе: " + allUsers.size());
//
//        } finally {
//            for (Long userId : userIds) {
//                if (userId != null) {
//                    userService.deleteUser(userId);
//                }
//            }
//            System.out.println("Тестовые пользователи удалены");
//        }
//    }
//}