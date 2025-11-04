package com.mipt.portal.moderator;

import com.mipt.portal.moderator.ModeratorRepository;
//import com.mipt.portal.announcement.AdsRepository;
import java.util.*;

public class ModeratorService {
    //private final AdsRepository adsRepository;
    private final ModeratorRepository moderatorRepository;
    private Moderator moderator;

    public ModeratorService(/*AdsRepository adsRepository,*/ ModeratorRepository moderatorRepository) {
        //this.adsRepository = adsRepository;
        this.moderatorRepository = moderatorRepository;
    }

    public boolean login(String email, String password) {
        Optional<Moderator> moderatorOpt = moderatorRepository.findByEmail(email);
        if (moderatorOpt.isPresent() && moderatorOpt.get().getPassword().equals(password)) {
            moderator = moderatorOpt.get();
            return true;
        }
        return false;
    }

    private void moderatePendingAds(Scanner scanner) {
        //List<Long> idPendingAds = adsRepository.findByStatus("pending");
        List<Long> idPendingAds = List.of(1L, 2L, 3L, 4L);

        if (idPendingAds.isEmpty()) {
            System.out.println("Нет объявлений для модерации!");
            return;
        }

        System.out.println("\n=======ОБЪЯВЛЕНИЯ НА МОДЕРАЦИИ=======\n" );

        for (int i = 0; i < idPendingAds.size(); i++) {
            Long adId = idPendingAds.get(i);
            System.out.println("\nОбъявление №" + (i + 1) + ".");
            //adsRepository.printAdById(adId);
            System.out.println("--объявление--\n");
        }

        System.out.print("\nВведите номер объявления для модерации (0 для выхода): ");

        try {
            int adNumber = scanner.nextInt();
            scanner.nextLine();

            if (adNumber == 0) return;
            if (adNumber < 1 || adNumber > idPendingAds.size()) {
                System.out.println("Неверный номер объявления!");
                return;
            }
            Long selectedAdId = idPendingAds.get(adNumber - 1);
            moderateSingleAd(selectedAdId, scanner);
        } catch (Exception e) {
            System.out.println("Ошибка ввода. Попробуйте еще раз.");
            scanner.nextLine();
        }
    }

    private void moderateSingleAd(Long id, Scanner scanner) {
        System.out.println("\nМОДЕРАЦИЯ ОБЪЯВЛЕНИЯ №" + id);
        //adsRepository.printAdById(adId);

        System.out.println("\nВыберите действие:");
        System.out.println("1. Одобрить объявление");
        System.out.println("2. Удалить объявление");
        System.out.println("3. Пропустить");
        System.out.print("Введите номер (1-3): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    approveAd(id, scanner);
                    break;
                case 2:
                    rejectAd(id, scanner);
                    break;
                case 3:
                    System.out.println("Объявление #" + id + " пропущено.");
                    break;
                default:
                    System.out.println("Неверный выбор");
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода. Попробуйте еще раз.");
            scanner.nextLine();
        }
    }

    private void approveAd(Long id, Scanner scanner) {
        System.out.println("Объявление одобрено.");
    }

    private void rejectAd(Long id, Scanner scanner) {
        //удаление из БД
        System.out.println("\n1. Нарушение правил платформы");
        System.out.println("2. Неполное описание, неправильно заполненная информация");
        System.out.println("3. Несоответсвие категории/тегам");
        System.out.println("4. Другая причина");
        System.out.println("Выберите причину удаления объявления (1-4): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice < 1 || choice > 4) {
                System.out.println("Причина не указана");
                choice = 0;
            }

            String reason = getReasonOfRejection(choice, scanner);

            //если удалено из базы данных успешно:
            System.out.println("Объявление отклонено по причине: " + reason);
            //если не удалено (ошибка):
            System.out.println("Объявление не найдено");
        } catch (Exception e) {
            System.out.println("Ошибка ввода. Причина не указана");
            String reason = getReasonOfRejection(0, scanner);

//            если удалено из базы данных успешно {
//              System.out.println("Объявление отклонено по причине: " + reason);
//            }
//            если не удалено (ошибка) {
//              System.out.println("Объявление не найдено");
//            }
        }
    }

    private String getReasonOfRejection(int choice, Scanner scanner) {
        switch (choice) {
            case 1: return "Нарушение правил платформы";
            case 2: return "Неполное описание, неправильно заполненная информация";
            case 3: return "Несоответсвие категории/тегам";
            case 4:
                System.out.println("Введите причину удаления: ");
                String reason = scanner.nextLine();
                return reason;
            default: return "Не указана";
        }
    }
}
