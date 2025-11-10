package com.mipt.portal.users.service;

import com.mipt.portal.users.User;
import com.mipt.portal.users.repository.UserRepository;
import com.mipt.portal.users.repository.UserRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Data
public class UserService {

    private UserRepository userRepository;

    public UserService() {
        try {
            this.userRepository = new UserRepositoryImpl();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize UserRepository", e);
        }
    }

    public User registerUser(String email, String name, String password, String address,
                             String studyProgram, int course) throws SQLException {
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с email " + email + " уже существует");
        }

        // Валидация входных данных
        validateUserData(email, name, password, studyProgram, course);

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password); // В реальном приложении пароль должен быть хеширован!
        user.setAddress(address);
        user.setStudyProgram(studyProgram);
        user.setCourse(course);
        user.setRating(0.0);
        user.setCoins(0);

        Optional<User> savedUser = userRepository.save(user);
        if (savedUser.isPresent()) {
            System.out.println("✅ Пользователь успешно зарегистрирован! ID: " + savedUser.get().getId());
            return savedUser.get();
        } else {
            throw new SQLException("Не удалось сохранить пользователя");
        }
    }

    public Optional<User> updateUser(User user) throws SQLException {

        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            System.out.println("❌ Пользователь с ID " + user.getId() + " не найден");
            return Optional.empty();
        }

        validateUserData(user.getEmail(), user.getName(), user.getPassword(),
                user.getStudyProgram(), user.getCourse());

        Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && userWithSameEmail.get().getId() != user.getId()) {
            throw new IllegalArgumentException("Email " + user.getEmail() + " уже занят другим пользователем");
        }

        boolean updated = userRepository.update(user);
        if (updated) {
            System.out.println("✅ Данные пользователя обновлены успешно!");
            return Optional.of(user);
        } else {
            System.out.println("❌ Не удалось обновить данные пользователя");
            return Optional.empty();
        }
    }

    public boolean deleteUser(long userId) throws SQLException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("❌ Пользователь с ID " + userId + " не найден");
            return false;
        }

        boolean deleted = userRepository.delete(userId);
        if (deleted) {
            System.out.println("✅ Пользователь успешно удален");
        } else {
            System.out.println("❌ Не удалось удалить пользователя");
        }
        return deleted;
    }

    public Optional<User> findUserById(long userId) throws SQLException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            System.out.println("✅ Пользователь найден: " + user.get().getEmail());
        } else {
            System.out.println("❌ Пользователь с ID " + userId + " не найден");
        }
        return user;
    }

    public Optional<User> findUserByEmail(String email) throws SQLException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("✅ Пользователь найден: " + user.get().getName());
        } else {
            System.out.println("❌ Пользователь с email " + email + " не найден");
        }
        return user;
    }

    public boolean updateUserRating(long userId, double newRating) throws SQLException {
        if (newRating < 0.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Рейтинг должен быть в диапазоне от 0.0 до 5.0");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRating(newRating);
            boolean updated = userRepository.update(user);
            if (updated) {
                System.out.println("✅ Рейтинг пользователя обновлен: " + newRating);
            }
            return updated;
        }
        return false;
    }

    public boolean updateUserCoins(long userId, int coins) throws SQLException {
        if (coins < 0) {
            throw new IllegalArgumentException("Количество коинов не может быть отрицательным");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCoins(coins);
            boolean updated = userRepository.update(user);
            if (updated) {
                System.out.println("✅ Коины пользователя обновлены: " + coins);
            }
            return updated;
        }
        return false;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = userRepository.findAll();
        System.out.println("📊 Найдено пользователей: " + users.size());
        return users;
    }

    public boolean existsByEmail(String email) throws SQLException {
        return userRepository.existsByEmail(email);
    }

    public boolean addCoins(long userId, int coinsToAdd) throws SQLException {
        if (coinsToAdd <= 0) {
            throw new IllegalArgumentException("Количество добавляемых коинов должно быть положительным");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setCoins(user.getCoins() + coinsToAdd);
            boolean updated = userRepository.update(user);
            if (updated) {
                System.out.println("✅ Добавлено " + coinsToAdd + " коинов. Текущий баланс: " + user.getCoins());
            }
            return updated;
        }
        return false;
    }

    public boolean deductCoins(long userId, int coinsToDeduct) throws SQLException {
        if (coinsToDeduct <= 0) {
            throw new IllegalArgumentException("Количество списываемых коинов должно быть положительным");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getCoins() < coinsToDeduct) {
                throw new IllegalArgumentException("Недостаточно коинов. Текущий баланс: " + user.getCoins() + ", требуется: " + coinsToDeduct);
            }
            user.setCoins(user.getCoins() - coinsToDeduct);
            boolean updated = userRepository.update(user);
            if (updated) {
                System.out.println("✅ Списано " + coinsToDeduct + " коинов. Текущий баланс: " + user.getCoins());
            }
            return updated;
        }
        return false;
    }

    private void validateUserData(String email, String name, String password,
                                  String studyProgram, int course) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        if (studyProgram == null || studyProgram.trim().isEmpty()) {
            throw new IllegalArgumentException("Учебная программа не может быть пустой");
        }
        if (course < 1 || course > 6) {
            throw new IllegalArgumentException("Курс должен быть в диапазоне от 1 до 6");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
    }
}