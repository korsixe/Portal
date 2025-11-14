<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.users.User" %>
<%@ page import="com.mipt.portal.announcement.AdsService" %>
<%@ page import="com.mipt.portal.announcement.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
    // Проверяем авторизацию пользователя
    User user = (User) session.getAttribute("user");

    // Получаем последние объявления (пока заглушка)
    AdsService adsService = new AdsService();
    List<Announcement> recentAds = new ArrayList<>(); // Заглушка - пустой список
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Portal - Главная</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            padding: 20px;
        }

        .home-container {
            max-width: 1200px;
            margin: 0 auto;
        }

        /* Шапка */
        .header {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 30px 40px;
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .portal-logo {
            font-size: 2.5rem;
            font-weight: 800;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 2px;
        }

        .search-section {
            flex: 1;
            max-width: 600px;
            min-width: 300px;
        }

        .search-form {
            display: flex;
            gap: 10px;
        }

        .search-input {
            flex: 1;
            padding: 15px 20px;
            border: 2px solid #e1e5e9;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .search-btn {
            padding: 15px 25px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .search-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
        }

        .auth-buttons {
            display: flex;
            gap: 10px;
        }

        /* Основной контент */
        .main-content {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 40px;
            min-height: 500px;
        }

        .section-title {
            color: #333;
            font-size: 2rem;
            margin-bottom: 30px;
            text-align: center;
        }

        /* Сетка объявлений */
        .ads-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }

        .ad-card {
            background: #f8f9fa;
            border-radius: 15px;
            padding: 25px;
            border-left: 4px solid #667eea;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .ad-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
        }

        .ad-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 12px;
            line-height: 1.3;
        }

        .ad-price {
            font-size: 1.5rem;
            font-weight: 700;
            color: #667eea;
            margin-bottom: 15px;
        }

        .ad-description {
            color: #666;
            line-height: 1.5;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .ad-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 15px;
        }

        .ad-category {
            background: #667eea;
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .ad-condition {
            background: #28a745;
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .ad-location {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .ad-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #e9ecef;
        }

        .ad-date {
            color: #999;
            font-size: 0.8rem;
        }

        .ad-views {
            color: #666;
            font-size: 0.8rem;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        /* Сообщение о пустом списке */
        .no-ads {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }

        .no-ads-icon {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }

        .no-ads h3 {
            font-size: 1.5rem;
            margin-bottom: 10px;
            color: #333;
        }

        .no-ads p {
            font-size: 1.1rem;
            line-height: 1.6;
            max-width: 500px;
            margin: 0 auto;
        }

        /* Кнопки */
        .btn {
            padding: 12px 25px;
            border: none;
            border-radius: 10px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: transparent;
            color: #667eea;
            border: 2px solid #667eea;
        }

        .btn-secondary:hover {
            background: #667eea;
            color: white;
            transform: translateY(-2px);
        }

        /* Адаптивность */
        @media (max-width: 768px) {
            .header {
                flex-direction: column;
                text-align: center;
            }

            .search-section {
                max-width: 100%;
            }

            .search-form {
                flex-direction: column;
            }

            .auth-buttons {
                justify-content: center;
            }

            .ads-grid {
                grid-template-columns: 1fr;
            }

            .section-title {
                font-size: 1.7rem;
            }
        }

        @media (max-width: 480px) {
            .header {
                padding: 20px;
            }

            .main-content {
                padding: 25px 20px;
            }

            .portal-logo {
                font-size: 2rem;
            }

            .btn {
                padding: 10px 20px;
                font-size: 0.9rem;
            }
        }
    </style>
</head>
<body>
<div class="home-container">
    <!-- Шапка с поиском -->
    <div class="header">
        <div class="portal-logo">PORTAL</div>

        <div class="search-section">
            <form class="search-form" method="GET" action="#">
                <input type="text"
                       class="search-input"
                       placeholder="🔍 Поиск объявлений..."
                       name="query">
                <button type="submit" class="search-btn">Найти</button>
            </form>
        </div>

        <div class="auth-buttons">
            <% if (user != null) { %>
            <a href="dashboard.jsp" class="btn btn-primary">Личный кабинет</a>
            <a href="index.jsp" class="btn btn-secondary">Выйти</a>
            <% } else { %>
            <a href="login.jsp" class="btn btn-secondary">Войти</a>
            <a href="register.jsp" class="btn btn-primary">Регистрация</a>
            <% } %>
        </div>
    </div>

    <!-- Основной контент -->
    <div class="main-content">
        <h1 class="section-title">🎯 Последние объявления</h1>

        <% if (recentAds.isEmpty()) { %>
        <!-- Сообщение, если объявлений нет -->
        <div class="no-ads">
            <div class="no-ads-icon">📭</div>
            <h3>Объявлений пока нет</h3>
            <p><em>Будьте первым, кто разместит объявление на нашей платформе!</em></p>
            <% if (user != null) { %>
            <a href="create-ad.jsp" class="btn btn-primary" style="margin-top: 20px;">
                + Создать первое объявление
            </a>
            <% } else { %>
            <p style="margin-top: 20px;">
                Войдите или зарегистрируйтесь, чтобы разместить объявление
            </p>
            <% } %>
        </div>
        <% } else { %>
        <!-- Сетка объявлений -->
        <div class="ads-grid">
            <% for (Announcement ad : recentAds) { %>
            <div class="ad-card" onclick="location.href='ad-details.jsp?id=<%= ad.getId() %>'">
                <div class="ad-title"><%= ad.getTitle() %></div>

                <div class="ad-price">
                    <%= formatPrice(ad.getPrice()) %>
                </div>

                <div class="ad-meta">
                    <span class="ad-category"><%= ad.getCategory().getDisplayName() %></span>
                    <span class="ad-condition"><%= ad.getCondition().getDisplayName() %></span>
                </div>

                <div class="ad-location">
                    📍 <%= ad.getLocation() %>
                </div>

                <div class="ad-description">
                    <%= ad.getDescription() %>
                </div>

                <div class="ad-footer">
                    <div class="ad-date">
                        📅 <%= formatDate(ad.getCreatedAt()) %>
                    </div>
                    <div class="ad-views">
                        👁️ <%= ad.getViewCount() != null ? ad.getViewCount() : 0 %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>

        <!-- Кнопка "Показать еще" -->
        <div style="text-align: center; margin-top: 30px;">
            <button class="btn btn-secondary" onclick="loadMoreAds()">
                📄 Показать еще объявления
            </button>
        </div>
        <% } %>
    </div>
</div>

<script>
    // Функция для загрузки дополнительных объявлений
    function loadMoreAds() {
        alert('Функция "Показать еще" будет реализована позже');
        // Здесь будет AJAX-запрос для подгрузки объявлений
    }

    // Обработка формы поиска
    document.querySelector('.search-form').addEventListener('submit', function(e) {
        e.preventDefault();
        const query = this.querySelector('.search-input').value.trim();
        if (query) {
            alert('Поиск по запросу: "' + query + '"\n\nФункция поиска будет реализована позже');
            // Здесь будет реализация поиска
        }
    });

    // Анимация появления карточек
    document.addEventListener('DOMContentLoaded', function() {
        const cards = document.querySelectorAll('.ad-card');
        cards.forEach((card, index) => {
            card.style.animationDelay = (index * 0.1) + 's';
            card.style.animation = 'fadeInUp 0.6s ease-out forwards';
        });
    });

    // Добавляем стили для анимации
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .ad-card {
            opacity: 0;
        }
    `;
    document.head.appendChild(style);
</script>
</body>
</html>

<%!
    // Вспомогательные методы для форматирования

    private String formatPrice(int price) {
        if (price == -1) {
            return "Договорная";
        } else if (price == 0) {
            return "Бесплатно";
        } else {
            return String.format("%,d руб.", price);
        }
    }

    private String formatDate(java.time.Instant instant) {
        if (instant == null) return "Не указано";
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        .withZone(java.time.ZoneId.systemDefault());
        return formatter.format(instant);
    }
%>