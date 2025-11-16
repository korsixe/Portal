package com.mipt.portal.users.service;

import com.mipt.portal.users.User;
import com.mipt.portal.users.repository.UserRepository;
import com.mipt.portal.users.repository.UserRepositoryImpl;
import com.mipt.portal.users.util.UserValidator;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public UserService() {
    try {
      this.userRepository = new UserRepositoryImpl();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to initialize UserService", e);
    }
  }

  public OperationResult<User> registerUser(String email, String name, String password,
      String passwordAgain, String address,
      String studyProgram, int course) {
    try {
      if (email == null || email.trim().isEmpty()) {
        return OperationResult.error("❌ Email не может быть пустым");
      }

      try {
        UserValidator.validateEmail(email);
      } catch (IllegalArgumentException e) {
        return OperationResult.error("❌ " + e.getMessage());
      }

      if (name == null || name.trim().isEmpty()) {
        return OperationResult.error("❌ Имя не может быть пустым");
      }

      try {
        UserValidator.validateName(name);
      } catch (IllegalArgumentException e) {
        return OperationResult.error("❌ " + e.getMessage());
      }

      if (password == null || password.trim().isEmpty()) {
        return OperationResult.error("❌ Пароль не может быть пустым");
      }

      try {
        UserValidator.validatePassword(password);
        UserValidator.isPasswordStrong(password);
      } catch (IllegalArgumentException e) {
        return OperationResult.error("❌ " + e.getMessage());
      }

      if (!password.equals(passwordAgain)) {
        return OperationResult.error("❌ Пароли не совпадают");
      }

      if (studyProgram == null || studyProgram.trim().isEmpty()) {
        return OperationResult.error("❌ Учебная программа не может быть пустой");
      }

      if (course < 1 || course > 6) {
        return OperationResult.error("❌ Курс должен быть в диапазоне от 1 до 6");
      }

      if (userRepository.existsByEmail(email)) {
        return OperationResult.error("❌ Пользователь с email " + email + " уже существует");
      }

      User user = new User();
      user.setEmail(email);
      user.setName(name);
      user.setPassword(password);
      user.setAddress(address != null ? address : "");
      user.setStudyProgram(studyProgram);
      user.setCourse(course);
      user.setRating(0.0);
      user.setCoins(0);

      Optional<User> savedUser = userRepository.save(user);
      if (savedUser.isPresent()) {
        return OperationResult.success(
            "🎉 Спасибо за регистрацию! Добро пожаловать в PORTAL!",
            savedUser.get()
        );
      } else {
        return OperationResult.error("❌ Не удалось сохранить пользователя в базу данных");
      }

    } catch (Exception e) {
      return OperationResult.error("❌ Неизвестная ошибка: " + e.getMessage());
    }
  }

  public OperationResult<User> loginUser(String email, String password) {
    try {
      if (email == null || email.trim().isEmpty()) {
        return OperationResult.error("❌ Email не может быть пустым");
      }

      try {
        UserValidator.validateEmail(email);
      } catch (IllegalArgumentException e) {
        return OperationResult.error("❌ " + e.getMessage());
      }

      if (password == null || password.trim().isEmpty()) {
        return OperationResult.error("❌ Пароль не может быть пустым");
      }

      Optional<User> userOpt = userRepository.findByEmail(email);
      if (userOpt.isEmpty()) {
        return OperationResult.error("❌ Пользователь с email " + email + " не найден");
      }

      User user = userOpt.get();

      if (!user.getPassword().equals(password)) {
        return OperationResult.error("❌ Неверный пароль");
      }

      return OperationResult.success("Добро пожаловать, " + user.getName() + "!", user);

    } catch (Exception e) {
      return OperationResult.error("❌ Неизвестная ошибка: " + e.getMessage());
    }
  }

  public OperationResult<User> updateUser(User user) {
    Optional<User> existingUser = userRepository.findById(user.getId());
    if (existingUser.isEmpty()) {
      return OperationResult.error("❌ Пользователь не найден");
    }
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
      user.setPassword(existingUser.get().getPassword());
    }
    try {
      UserValidator.validateEmail(user.getEmail());
      UserValidator.validateName(user.getName());
      UserValidator.validatePassword(user.getPassword());
      UserValidator.isPasswordStrong(user.getPassword());
    } catch (IllegalArgumentException e) {
      return OperationResult.error("❌ " + e.getMessage());
    }

    Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());
    if (userWithSameEmail.isPresent() && userWithSameEmail.get().getId() != (user.getId())) {
      return OperationResult.error(
          "❌ Email " + user.getEmail() + " уже занят другим пользователем");
    }

    boolean updated = userRepository.update(user);
    if (updated) {
      return OperationResult.success("✅ Данные пользователя обновлены успешно!", user);
    } else {
      return OperationResult.error("❌ Не удалось обновить данные пользователя");
    }
  }

  public OperationResult<Boolean> addAnnouncementId(Long userId, Long adId) {
    try {
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        return OperationResult.error("❌ Пользователь не найден");
      }

      User user = userOpt.get();

      if (user.getAdList() == null) {
        user.setAdList(new ArrayList<>());
      }

      if (!user.getAdList().contains(adId)) {
        user.getAdList().add(adId);

        boolean updated = userRepository.update(user);
        if (updated) {
          return OperationResult.success("✅ ID объявления добавлен в список пользователя", true);
        } else {
          return OperationResult.error("❌ Не удалось обновить данные пользователя в БД");
        }
      } else {
        return OperationResult.success("✅ ID объявления уже присутствует в списке", true);
      }
    } catch (Exception e) {
      return OperationResult.error("❌ Ошибка при добавлении ID объявления: " + e.getMessage());
    }
  }

  public OperationResult<Boolean> deleteAnnouncementId(Long userId, Long adId) {
    try {
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        return OperationResult.error("Пользователь не найден");
      }

      User user = userOpt.get();

      if (user.getAdList() == null || user.getAdList().isEmpty()) {
        return OperationResult.error("У пользователя нет объявлений");
      }

      if (user.getAdList().contains(adId)) {
        user.getAdList().remove(adId);
        boolean updated = userRepository.update(user);

        if (updated) {
          return OperationResult.success("ID объявления удалено");
        } else {
          return OperationResult.success("Не удалось удалить ID объявления");
        }
      } else {
        return OperationResult.error("Данное объявление не найдено у пользователя");
      }
    } catch (Exception e) {
      return OperationResult.error("Ошибка при удалении объявления: " + e);
    }
  }

  public OperationResult<Boolean> deleteUser(long userId) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return OperationResult.error("❌ Пользователь не найден");
    }

    boolean deleted = userRepository.delete(userId);
    if (deleted) {
      return OperationResult.success("✅ Пользователь успешно удален", true);
    } else {
      return OperationResult.error("❌ Не удалось удалить пользователя");
    }
  }

  public OperationResult<User> findUserById(long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      return OperationResult.success("✅ Пользователь найден", user.get());
    } else {
      return OperationResult.error("❌ Пользователь с ID " + userId + " не найден");
    }
  }

  public OperationResult<User> findUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      return OperationResult.success("✅ Пользователь найден", user.get());
    } else {
      return OperationResult.error("❌ Пользователь с email " + email + " не найден");
    }
  }

  public OperationResult<Boolean> updateUserRating(long userId, double newRating) {
    if (newRating < 0.0 || newRating > 5.0) {
      return OperationResult.error("❌ Рейтинг должен быть в диапазоне от 0.0 до 5.0");
    }

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      user.setRating(newRating);
      boolean updated = userRepository.update(user);
      if (updated) {
        return OperationResult.success("✅ Рейтинг пользователя обновлен: " + newRating, true);
      }
    }
    return OperationResult.error("❌ Не удалось обновить рейтинг пользователя");
  }

  public List<User> getAllUsers() throws SQLException {
    return userRepository.findAll();
  }

  public boolean existsByEmail(String email) throws SQLException {
    return userRepository.existsByEmail(email);
  }

  public OperationResult<Boolean> addCoins(long userId, int coinsToAdd) {
    if (coinsToAdd <= 0) {
      return OperationResult.error("❌ Количество добавляемых коинов должно быть положительным");
    }

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      user.setCoins(user.getCoins() + coinsToAdd);
      boolean updated = userRepository.update(user);
      if (updated) {
        return OperationResult.success(
            "✅ Добавлено " + coinsToAdd + " коинов. Текущий баланс: " + user.getCoins(),
            true
        );
      }
    }
    return OperationResult.error("❌ Не удалось добавить коины");
  }

  public OperationResult<Boolean> deductCoins(long userId, int coinsToDeduct) {
    if (coinsToDeduct <= 0) {
      return OperationResult.error("❌ Количество списываемых коинов должно быть положительным");
    }

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (user.getCoins() < coinsToDeduct) {
        return OperationResult.error(
            "❌ Недостаточно коинов. Текущий баланс: " + user.getCoins() + ", требуется: "
                + coinsToDeduct
        );
      }
      user.setCoins(user.getCoins() - coinsToDeduct);
      boolean updated = userRepository.update(user);
      if (updated) {
        return OperationResult.success(
            "✅ Списано " + coinsToDeduct + " коинов. Текущий баланс: " + user.getCoins(),
            true
        );
      }
    }
    return OperationResult.error("❌ Не удалось списать коины");
  }
}