package com.mipt.portal.testing_user;

import com.mipt.portal.service.UserRegistrationImpl;
import com.mipt.portal.service.User;
import java.util.List;
import java.util.Scanner;


public class Application {
  public static void main(String[] args) {
    UserRegistrationImpl registration = new UserRegistrationImpl();
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
    String password = scanner.nextLine();
    User user = registration.register(email, name, password);
    System.out.println(user);

    if (user != null) {
      System.out.println("\n\uD83C\uDF89 Спасибо за регистрацию!");
    }
  }
}