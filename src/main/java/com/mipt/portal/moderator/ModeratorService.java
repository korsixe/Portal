package com.mipt.portal.moderator;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.moderator.ModeratorRepository;
import com.mipt.portal.announcement.AdsService;

import java.sql.SQLException;
import java.util.*;

public class ModeratorService {
    private final AdsService adsService;
    private final ModeratorRepository moderatorRepository;
    private Moderator moderator;

    public ModeratorService(AdsService adsService, ModeratorRepository moderatorRepository) {
        this.adsService = adsService;
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

    private void moderatePendingAds() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        List<Long> pendingAdsList = adsService.getModerAdIds();

        if (pendingAdsList.isEmpty()) {
            System.out.println("Нет объявлений для модерации!");
            return;
        }

        System.out.println("\n=======ОБЪЯВЛЕНИЯ НА МОДЕРАЦИИ=======\n" );

        for (int i = 0; i < pendingAdsList.size(); i++) {
            Long adId = pendingAdsList.get(i);
            System.out.println("\nОбъявление №" + (i + 1) + ".");
            adsService.getAd(adId);
            System.out.println("--объявление--\n");
        }

        System.out.print("\nВведите номер объявления для модерации (0 для выхода): ");

        try {
            int adNumber = scanner.nextInt();
            scanner.nextLine();

            if (adNumber == 0) return;
            if (adNumber < 1 || adNumber > pendingAdsList.size()) {
                System.out.println("Неверный номер объявления!");
                return;
            }
            Long selectedAdId = pendingAdsList.get(adNumber - 1);
            moderateSingleAd(selectedAdId);
        } catch (Exception e) {
            System.out.println("Ошибка ввода. Попробуйте еще раз.");
            scanner.nextLine();
        }
    }

    private void moderateSingleAd(Long id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nМОДЕРАЦИЯ ОБЪЯВЛЕНИЯ №" + id);
        Announcement ad = adsService.getAd(id);
        System.out.println(ad);

        System.out.println("\nВыберите действие:");
        System.out.println("1. Одобрить объявление");
        System.out.println("2. Отозвать объявление на доработку");
        System.out.println("3. Удалить объявление");
        System.out.println("4. Пропустить");
        System.out.print("Введите номер (1-4): ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    approveAd(ad);
                    break;
                case 2:
                    rejectAd(ad);
                    break;
                case 3:
                    deleteAd(ad);
                    break;
                case 4:
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

    private void approveAd(Announcement ad) throws SQLException {
        adsService.activate(ad);
        System.out.println("Объявление одобрено.");
    }

    private void rejectAd(Announcement ad) {
        Scanner scanner = new Scanner(System.in);

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
            ad.rejectModeration();

        } catch (Exception e) {
            System.out.println("Ошибка ввода. Причина не указана");
            String reason = getReasonOfRejection(0, scanner);
            ad.rejectModeration();
        }
    }

    private void deleteAd(Announcement ad) {
        ad.delete();
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
