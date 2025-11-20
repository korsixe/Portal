<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.users.User" %>
<%@ page import="com.mipt.portal.announcement.AdsService" %>
<%@ page import="com.mipt.portal.announcement.Announcement" %>
<%@ page import="com.mipt.portal.announcement.Category" %>
<%@ page import="com.mipt.portal.announcement.Condition" %>
<%@ page import="com.mipt.portal.announcement.AdvertisementStatus" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.mipt.portal.users.service.UserService" %>
<%@ page import="com.mipt.portal.notifications.NotificationService" %>
<%@ page import="com.mipt.portal.moderator.message.ModerationMessage" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    UserService userService = new UserService();
    User freshUser = userService.findUserById(user.getId()).getData();
    session.setAttribute("user", freshUser);
    user = freshUser; // Обновляем локальную переменную

    AdsService adsService = new AdsService();
    List<Announcement> userAnnouncements = new ArrayList<>();

    if (user.getAdList() != null && !user.getAdList().isEmpty()) {
        for (Long adId : user.getAdList()) {
            try {
                Announcement ad = adsService.getAd(adId);
                if (ad != null && !ad.getStatus().isDelete()) {
                    userAnnouncements.add(ad);
                }
            } catch (Exception e) {
                // Пропускаем объявления, которые не удалось загрузить
                System.err.println("Ошибка при загрузке объявления ID " + adId + ": " + e.getMessage());
            }
        }
    }

    // Загружаем уведомления пользователя
    NotificationService notificationService = new NotificationService();
    List<ModerationMessage> userNotifications = notificationService.getUserNotifications(user.getId());
    int unreadCount = notificationService.getUnreadCount(user.getId());
%>
<%
    // В начале home.jsp, после проверки авторизации
    String message = request.getParameter("message");
    if (message != null) {
%>
<script>
    alert('<%= message %>');
</script>
<%
    }
%>
<%
    // Проверяем сообщения от обработчиков
    String passwordMessage = (String) session.getAttribute("passwordMessage");
    String passwordMessageType = (String) session.getAttribute("passwordMessageType");
    String deleteMessage = (String) session.getAttribute("deleteMessage");
    String deleteMessageType = (String) session.getAttribute("deleteMessageType");

    if (passwordMessage != null) {
%>
<script>
    alert("<%= passwordMessage %>");
</script>
<%
        session.removeAttribute("passwordMessage");
        session.removeAttribute("passwordMessageType");
    }

    if (deleteMessage != null) {
%>
<script>
    alert("<%= deleteMessage %>");
</script>
<%
        session.removeAttribute("deleteMessage");
        session.removeAttribute("deleteMessageType");
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Portal - Личный кабинет</title>
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

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .header {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 40px;
            margin-bottom: 30px;
        }

        .header-top {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }

        .portal-logo {
            font-size: 3.5rem;
            font-weight: 800;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: 2px;
        }



        .welcome-message {
            color: #666;
            font-size: 1.5rem;
            text-align: center;
        }

        /* Стили для уведомлений */
        .notification-container {
            position: relative;
            display: inline-block;
        }

        .notification-bell {
            position: relative;
            background: none;
            border: none;
            font-size: 1.8rem;
            cursor: pointer;
            padding: 10px;
            border-radius: 50%;
            transition: all 0.3s ease;
            color: #667eea;
        }

        .notification-bell:hover {
            background: rgba(102, 126, 234, 0.1);
            transform: scale(1.1);
        }

        .notification-badge {
            position: absolute;
            top: 5px;
            right: 5px;
            background: #dc3545;
            color: white;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            font-size: 0.8rem;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
        }

        .notification-dropdown {
            display: none;
            position: absolute;
            top: 100%;
            right: 0;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            width: 400px;
            max-height: 500px;
            overflow-y: auto;
            z-index: 1000;
            margin-top: 10px;
        }

        .notification-dropdown.show {
            display: block;
            animation: fadeInUp 0.3s ease-out;
        }

        .notification-header {
            padding: 20px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .notification-header h4 {
            margin: 0;
            color: #333;
        }

        .notification-list {
            padding: 0;
            margin: 0;
            list-style: none;
        }

        .notification-item {
            padding: 15px 20px;
            border-bottom: 1px solid #f8f9fa;
            cursor: pointer;
            transition: background 0.3s ease;
            display: flex;
            align-items: flex-start;
            gap: 12px;
        }

        .notification-item:hover {
            background: #f8f9fa;
        }

        .notification-item.unread {
            background: #f0f7ff;
            border-left: 3px solid #667eea;
        }

        .notification-icon {
            font-size: 1.2rem;
            margin-top: 2px;
            flex-shrink: 0;
        }

        .notification-content {
            flex: 1;
        }

        .notification-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
            font-size: 0.9rem;
        }

        .notification-message {
            color: #666;
            font-size: 0.85rem;
            line-height: 1.4;
            margin-bottom: 5px;
        }

        .notification-reason {
            color: #888;
            font-size: 0.8rem;
            font-style: italic;
        }

        .notification-time {
            color: #999;
            font-size: 0.75rem;
        }

        .notification-actions {
            padding: 15px 20px;
            border-top: 1px solid #eee;
            text-align: center;
        }

        .no-notifications {
            padding: 40px 20px;
            text-align: center;
            color: #666;
        }

        .no-notifications .icon {
            font-size: 3rem;
            margin-bottom: 10px;
            opacity: 0.5;
        }

        .user-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .info-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .info-card h3 {
            color: #333;
            margin-bottom: 15px;
            font-size: 1.3rem;
        }

        .info-item {
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            border-bottom: 1px solid #eee;
            padding-bottom: 8px;
        }

        .info-label {
            font-weight: 600;
            color: #555;
        }

        .info-value {
            color: #333;
        }

        .profile-actions {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin: 30px 0;
            flex-wrap: wrap;
        }

        .ads-section {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }

        .ads-section h3 {
            color: #333;
            margin-bottom: 20px;
            font-size: 1.3rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .ad-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }

        .ad-item {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 20px;
            border-left: 4px solid #667eea;
            transition: all 0.3s ease;
        }

        .ad-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }

        .ad-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
        }

        .ad-description {
            color: #666;
            margin-bottom: 10px;
            line-height: 1.4;
            max-height: 60px;
            overflow: hidden;
        }

        .ad-price {
            font-size: 1.3rem;
            font-weight: 700;
            color: #667eea;
            margin-bottom: 10px;
        }

        .ad-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 15px;
        }

        .ad-category {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
        }

        .ad-condition {
            display: inline-block;
            background: #28a745;
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
        }

        .ad-status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .status-active {
            background: #d4edda;
            color: #155724;
        }

        .status-draft {
            background: #fff3cd;
            color: #856404;
        }

        .status-moderation {
            background: #cce5ff;
            color: #004085;
        }

        .status-archived {
            background: #e2e3e5;
            color: #383d41;
        }

        .ad-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }

        .no-ads {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 40px;
            grid-column: 1 / -1;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }

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

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #218838;
            transform: translateY(-2px);
        }

        .btn-danger {
            background: #dc3545;
            color: white;
            padding: 8px 15px;
            font-size: 0.9rem;
        }

        .btn-danger:hover {
            background: #c82333;
        }

        .btn-edit {
            background: #ffc107;
            color: black;
            padding: 8px 15px;
            font-size: 0.9rem;
        }

        .btn-edit:hover {
            background: #e0a800;
        }

        .rating-stars {
            color: #ffc107;
            font-size: 1.2rem;
        }

        .coins {
            color: #ffd700;
            font-weight: 600;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }

        .stat-card {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
        }

        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .stat-label {
            font-size: 0.9rem;
            opacity: 0.9;
        }

        .ad-views {
            color: #666;
            font-size: 0.8rem;
            margin-top: 5px;
        }

        .ad-date {
            color: #999;
            font-size: 0.8rem;
            margin-top: 5px;
        }

        .ad-location {
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 10px;
        }

        /* Стили для модальных окон */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
            width: 90%;
            max-width: 500px;
            position: relative;
            animation: modalSlideIn 0.3s ease-out;
        }

        @keyframes modalSlideIn {
            from {
                opacity: 0;
                transform: translateY(-50px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .close {
            position: absolute;
            right: 20px;
            top: 15px;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            color: #999;
        }

        .close:hover {
            color: #333;
        }

        .modal h3 {
            color: #333;
            margin-bottom: 20px;
            font-size: 1.5rem;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }

        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e1e5e9;
            border-radius: 10px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .warning-box {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 10px;
            padding: 15px;
            margin: 15px 0;
            text-align: center;
        }

        .warning-box h4 {
            color: #856404;
            margin-bottom: 10px;
        }

        @media (max-width: 768px) {
            .header-top {
                flex-direction: column;
                gap: 15px;
            }

            .notification-dropdown {
                width: 300px;
                right: -50px;
            }

            .user-info {
                grid-template-columns: 1fr;
            }

            .ad-list {
                grid-template-columns: 1fr;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }

            .stats {
                grid-template-columns: repeat(2, 1fr);
            }

            .ads-section h3 {
                flex-direction: column;
                gap: 10px;
                text-align: center;
            }

            .profile-actions {
                flex-direction: column;
                align-items: center;
            }

            .profile-actions .btn {
                width: 200px;
            }
        }
    </style>
</head>
<body>
<div class="dashboard-container">
    <div class="header">
        <div class="header-top">
            <div class="portal-logo">PORTAL</div>
        </div>
    </div>

    <div class="header-top">

        <!-- Подключаем колокольчик -->
        <jsp:include page="notification-bell.jsp" />
    </div>

    <!-- Статистика -->
    <div class="stats">
        <div class="stat-card">
            <div class="stat-number"><%= userAnnouncements.size() %></div>
            <div class="stat-label">Объявлений</div>
        </div>
        <div class="stat-card">
            <div class="stat-number"><%= String.format("%.1f", user.getRating()) %></div>
            <div class="stat-label">Рейтинг</div>
        </div>
        <div class="stat-card">
            <div class="stat-number"><%= user.getCoins() %></div>
            <div class="stat-label">Коинов</div>
        </div>
        <div class="stat-card">
            <div class="stat-number"><%= user.getCourse() %></div>
            <div class="stat-label">Курс</div>
        </div>
    </div>

    <div class="user-info">
        <div class="info-card">
            <h3>👤 Основная информация</h3>
            <div class="info-item">
                <span class="info-label">Имя:</span>
                <span class="info-value"><%= user.getName() %></span>
            </div>
            <div class="info-item">
                <span class="info-label">Email:</span>
                <span class="info-value"><%= user.getEmail() %></span>
            </div>
            <div class="info-item">
                <span class="info-label">Адрес:</span>
                <span class="info-value"><%= user.getAddress() != null && !user.getAddress().isEmpty() ? user.getAddress() : "Не указан" %></span>
            </div>
        </div>

        <div class="info-card">
            <h3>🎓 Учебная информация</h3>
            <div class="info-item">
                <span class="info-label">Учебная программа:</span>
                <span class="info-value"><%= user.getStudyProgram() %></span>
            </div>
            <div class="info-item">
                <span class="info-label">Курс:</span>
                <span class="info-value"><%= user.getCourse() %> курс</span>
            </div>
        </div>

        <div class="info-card">
            <h3>⭐ Рейтинг и коины</h3>
            <div class="info-item">
                <span class="info-label">Рейтинг:</span>
                <span class="info-value">
                    <span class="rating-stars">
                        <% for (int i = 0; i < 5; i++) { %>
                            <%= i < Math.round(user.getRating()) ? "★" : "☆" %>
                        <% } %>
                    </span>
                    (<%= String.format("%.1f", user.getRating()) %>)
                </span>
            </div>
            <div class="info-item">
                <span class="info-label">Коины:</span>
                <span class="info-value coins"><%= user.getCoins() %> 🪙</span>
            </div>
        </div>
    </div>

    <!-- Кнопки управления профилем -->
    <div class="profile-actions">
        <a href="edit-profile.jsp" class="btn btn-primary">Редактировать профиль</a>
        <button onclick="openAccountManagement()" class="btn btn-secondary">Управление аккаунтом</button>
    </div>

    <div class="ads-section">
        <h3>
            📋 Мои объявления
            <a href="create-ad.jsp" class="btn btn-success">+ Создать объявление</a>
        </h3>

        <div class="ad-list">
            <% if (userAnnouncements.isEmpty()) { %>
            <div class="no-ads">
                <h4>У вас пока нет объявлений</h4>
                <p>Создайте первое объявление, чтобы начать продавать или обмениваться вещами!</p>
            </div>
            <% } else { %>
            <% for (Announcement ad : userAnnouncements) { %>
            <div class="ad-item">
                <div class="ad-title"><%= ad.getTitle() %></div>

                <div class="ad-meta">
                    <span class="ad-category"><%= ad.getCategory().getDisplayName() %></span>
                    <span class="ad-condition"><%= ad.getCondition().getDisplayName() %></span>
                    <span class="ad-status <%= getStatusClass(ad.getStatus()) %>">
                        <%= ad.getStatus().getDisplayName() %>
                    </span>
                </div>

                <div class="ad-price">
                    <%= formatPrice(ad.getPrice()) %>
                </div>

                <div class="ad-location">📍 <%= ad.getLocation() %></div>

                <div class="ad-description"><%= ad.getDescription() %></div>

                <div class="ad-views">👁️ <%= ad.getViewCount() != null ? ad.getViewCount() : 0 %> просмотров</div>
                <div class="ad-date">📅 <%= formatDate(ad.getCreatedAt()) %></div>

                <div class="ad-actions">
                    <a href="edit-ad?adId=<%= ad.getId() %>" class="btn btn-edit">Редактировать</a>
                    <a href="confirm-delete?adId=<%= ad.getId() %>" class="btn btn-danger">Удалить</a>
                </div>
            </div>
            <% } %>
            <% } %>
        </div>
    </div>

    <div class="action-buttons">
        <a href="home.jsp" class="btn btn-primary">На главную</a>
        <a href="logout.jsp" class="btn btn-secondary">Выйти</a>
    </div>
</div>

<!-- Модальное окно управления аккаунтом -->
<div id="accountManagementModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeAccountManagement()">&times;</span>
        <h3>🔧 Управление аккаунтом</h3>

        <div class="button-group" style="display: flex; flex-direction: column; gap: 15px;">
            <button onclick="openChangePassword()" class="btn btn-primary">Изменить пароль</button>
            <button onclick="openDeleteAccount()" class="btn btn-danger">Удалить аккаунт</button>
        </div>
    </div>
</div>

<!-- Модальное окно изменения пароля -->
<div id="changePasswordModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeChangePassword()">&times;</span>
        <h3>🔐 Изменение пароля</h3>

        <form id="changePasswordForm" method="POST" action="change-password-handler.jsp">
            <input type="hidden" name="action" value="changePassword">

            <div class="form-group">
                <label for="currentPassword">Текущий пароль</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>

            <div class="form-group">
                <label for="newPassword">Новый пароль</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Подтверждение нового пароля</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <div class="button-group" style="display: flex; gap: 10px; margin-top: 20px;">
                <button type="submit" class="btn btn-primary">Сохранить пароль</button>
                <button type="button" onclick="closeChangePassword()" class="btn btn-secondary">Отмена</button>
            </div>
        </form>
    </div>
</div>

<!-- Модальное окно удаления аккаунта -->
<div id="deleteAccountModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeDeleteAccount()">&times;</span>
        <h3>🗑️ Удаление аккаунта</h3>

        <div class="warning-box">
            <h4>⚠️ Внимание!</h4>
            <p>Это действие необратимо. Все ваши данные, включая объявления, будут удалены без возможности восстановления.</p>
        </div>

        <p>Для подтверждения введите ваш пароль:</p>

        <form id="deleteAccountForm" method="POST" action="delete-account-handler.jsp">
            <input type="hidden" name="action" value="deleteAccount">

            <div class="form-group">
                <label for="confirmPasswordDelete">Текущий пароль</label>
                <input type="password" id="confirmPasswordDelete" name="confirmPassword" required>
            </div>

            <div class="button-group" style="display: flex; gap: 10px; margin-top: 20px;">
                <button type="submit" class="btn btn-danger">Удалить аккаунт</button>
                <button type="button" onclick="closeDeleteAccount()" class="btn btn-secondary">Отмена</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Функции для управления модальными окнами
    function openAccountManagement() {
        document.getElementById('accountManagementModal').style.display = 'block';
    }

    function closeAccountManagement() {
        document.getElementById('accountManagementModal').style.display = 'none';
    }

    function openChangePassword() {
        closeAccountManagement();
        document.getElementById('changePasswordModal').style.display = 'block';
    }

    function closeChangePassword() {
        document.getElementById('changePasswordModal').style.display = 'none';
    }

    function openDeleteAccount() {
        closeAccountManagement();
        document.getElementById('deleteAccountModal').style.display = 'block';
    }

    function closeDeleteAccount() {
        document.getElementById('deleteAccountModal').style.display = 'none';
    }

    // Функции для уведомлений
    function toggleNotifications() {
        const dropdown = document.getElementById('notificationDropdown');
        dropdown.classList.toggle('show');
    }

    function handleNotificationClick(adId) {
        // Переходим к объявлению
        window.location.href = 'http://localhost:8080/portal/edit-ad?adId=' + adId;
    }

    function markAllAsRead() {
        // Просто скрываем бейдж и закрываем dropdown
        const badge = document.getElementById('notificationBadge');
        if (badge) {
            badge.style.display = 'none';
        }
        document.getElementById('notificationDropdown').classList.remove('show');
    }

    // Закрытие модальных окон при клике вне их
    window.onclick = function(event) {
        const modals = document.getElementsByClassName('modal');
        for (let modal of modals) {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }

        // Закрытие dropdown уведомлений при клике вне
        const dropdown = document.getElementById('notificationDropdown');
        const bell = document.querySelector('.notification-bell');
        if (dropdown && bell && !bell.contains(event.target) && !dropdown.contains(event.target)) {
            dropdown.classList.remove('show');
        }
    }

    // Валидация формы изменения пароля
    document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (newPassword !== confirmPassword) {
            e.preventDefault();
            alert('❌ Пароли не совпадают!');
            return false;
        }

        if (newPassword.length < 8) {
            e.preventDefault();
            alert('❌ Пароль должен содержать минимум 8 символов!');
            return false;
        }
    });

    // Валидация формы удаления аккаунта
    document.getElementById('deleteAccountForm').addEventListener('submit', function(e) {
        if (!confirm('❗ Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить!')) {
            e.preventDefault();
            return false;
        }
    });

    // Добавляем анимации при загрузке
    document.addEventListener('DOMContentLoaded', function() {
        const cards = document.querySelectorAll('.info-card, .ad-item, .stat-card');
        cards.forEach((card, index) => {
            card.style.animationDelay = (index * 0.1) + 's';
            card.style.animation = 'fadeInUp 0.6s ease-out forwards';
        });
    });

    // Стили для анимации
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    `;
    document.head.appendChild(style);
</script>
</body>
</html>

<%!
            // Вспомогательные методы для JSP

            private String getStatusClass(AdvertisementStatus status) {
                switch (status) {
                    case ACTIVE: return "status-active";
                    case DRAFT: return "status-draft";
                    case UNDER_MODERATION: return "status-moderation";
                    case ARCHIVED: return "status-archived";
                    default: return "status-draft";
                }
            }

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
                if (instant == null) return "Не указано";
                java.time.format.DateTimeFormatter formatter =
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                .withZone(java.time.ZoneId.systemDefault());
                return formatter.format(instant);
            }
%>
