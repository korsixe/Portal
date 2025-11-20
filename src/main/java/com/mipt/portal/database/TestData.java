package com.mipt.portal.database;

import com.mipt.portal.announcement.AdsRepository;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.AdvertisementStatus;
import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;

import java.sql.SQLException;

public class TestData {
    static AdsService adsService = new AdsService();
    static AdsRepository adsRepository;

    static {
        try {
            adsRepository = new AdsRepository();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TestData() throws SQLException {
    }

    private static void start() {
        adsRepository.createTables();
        adsRepository.insertData();
        generateTestAds();
    }

    private static void generateTestAds() {
        try {
            // Анастасия Шабунина - 10 объявлений на доработку
            Long userId1 = adsService.getUserIdByEmail("shabunina.ao@phystech.edu");
            if (userId1 != null) {
                // 10 объявлений с статусом UNDER_MODERATION (на модерации/доработке)
                testCreateAd(userId1, "MacBook Pro 13\" 2020", "Отличный MacBook в идеальном состоянии",
                        Category.ELECTRONICS, Condition.NEW, 75000, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Учебник по матану", "Сборник задач за 1 курс", Category.BOOKS,
                        Condition.NEW, 500, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Настольная лампа", "Светодиодная лампа с регулировкой",
                        Category.ELECTRONICS, Condition.USED, 1200, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Калькулятор Casio", "Инженерный калькулятор", Category.ELECTRONICS,
                        Condition.BROKEN, 800, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Велосипед горный", "Горный велосипед для прогулок", Category.SPORTS,
                        Condition.USED, 15000, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Кофемашина DeLonghi", "Автоматическая кофемашина", Category.HOME,
                        Condition.NEW, 25000, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Фотокамера Canon", "Зеркальная камера для начинающих", Category.ELECTRONICS,
                        Condition.USED, 18000, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Гитара акустическая", "Шестиструнная гитара Yamaha", Category.HOBBY,
                        Condition.USED, 5000, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Микроскоп учебный", "Школьный микроскоп для биологии", Category.OTHER,
                        Condition.NEW, 3500, AdvertisementStatus.UNDER_MODERATION);

                testCreateAd(userId1, "Роликовые коньки", "Ролики разммер 38", Category.SPORTS,
                        Condition.USED, 2000, AdvertisementStatus.UNDER_MODERATION);
            }

            // Мария Соколова
            Long userId2 = adsService.getUserIdByEmail("ivanov.ii@phystech.edu");
            if (userId2 != null) {
                testCreateAd(userId2, "Учебник по физике", "Курс общей физики Ландсберга", Category.BOOKS,
                        Condition.USED, 1500, AdvertisementStatus.ACTIVE);
                testCreateAd(userId2, "Микроскоп школьный", "Детский микроскоп для начинающих",
                        Category.CHILDREN,
                        Condition.USED, 2000, AdvertisementStatus.ACTIVE);
                testCreateAd(userId2, "Рюкзак студенческий", "Вместительный рюкзак для ноутбука",
                        Category.OTHER, Condition.USED, 800, AdvertisementStatus.DRAFT);
            }

            // Дмитрий Орлов
            Long userId3 = adsService.getUserIdByEmail("orlov.ka@phystech.edu");
            if (userId3 != null) {
                testCreateAd(userId3, "Игровой компьютер", "Gaming PC для учебы и игр",
                        Category.ELECTRONICS,
                        Condition.USED, 45000, AdvertisementStatus.ACTIVE);
                testCreateAd(userId3, "Клавиатура механическая", "Mechanical keyboard с RGB",
                        Category.ELECTRONICS, Condition.NEW, 3500, AdvertisementStatus.ACTIVE);
                testCreateAd(userId3, "Стул офисный", "Офисный стул с регулировкой", Category.HOME,
                        Condition.NEW, 2500, AdvertisementStatus.UNDER_MODERATION);
                testCreateAd(userId3, "Книги по программированию", "Java, Python, Algorithms",
                        Category.BOOKS,
                        Condition.USED, 1200, AdvertisementStatus.DRAFT);
            }

            // Валерия Новикова
            Long userId4 = adsService.getUserIdByEmail("novikova.vv@phystech.edu");
            if (userId4 != null) {
                testCreateAd(userId4, "Микроскоп лабораторный", "Профессиональный для исследований",
                        Category.OTHER, Condition.NEW, 15000, AdvertisementStatus.ACTIVE);
                testCreateAd(userId4, "Набор реактивов", "Для студенческих опытов", Category.OTHER,
                        Condition.BROKEN, 3000, AdvertisementStatus.ACTIVE);
                testCreateAd(userId4, "Лабораторный халат", "Белый халат размер M", Category.CLOTHING,
                        Condition.USED, 500, AdvertisementStatus.UNDER_MODERATION);
            }

            System.out.println("✅ Тестовые объявления созданы!");
            System.out.println("✅ Анастасия Шабунина: 10 объявлений на доработке");

        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static private void testCreateAd(Long userId, String title, String description, Category category,
                                     Condition condition, int price, AdvertisementStatus status) throws SQLException {
        Announcement ad = new Announcement(title, description, category, condition, price,
                "МФТИ, Долгопрудный", userId);
        ad.setStatus(status);
        ad.setId(adsService.getAdsRepository().saveAd(ad));
        adsService.getUserService().addAnnouncementId(ad.getUserId(), ad.getId());
    }

    public static void main(String[] args) {
        start();
    }
}
