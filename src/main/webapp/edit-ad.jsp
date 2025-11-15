<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.announcement.Announcement" %>
<%@ page import="com.mipt.portal.announcement.Category" %>
<%@ page import="com.mipt.portal.announcement.Condition" %>
<%@ page import="com.mipt.portal.announcement.AdvertisementStatus" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Announcement announcement = (Announcement) request.getAttribute("announcement");
    if (announcement == null) {
        response.sendRedirect("dashboard");
        return;
    }
%>
<html>
<head>
    <title>Редактировать объявление • Portal</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
      :root {
        --primary: #4361ee;
        --primary-dark: #3a56d4;
        --secondary: #7209b7;
        --success: #4cc9f0;
        --danger: #f72585;
        --warning: #f8961e;
        --light: #f8f9fa;
        --dark: #212529;
        --gray: #6c757d;
        --border: #e9ecef;
        --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
      }

      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }

      body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        padding: 20px;
        line-height: 1.6;
      }

      .container {
        max-width: 800px;
        margin: 0 auto;
      }

      .header {
        text-align: center;
        margin-bottom: 30px;
      }

      .logo {
        font-size: 2.5rem;
        font-weight: 700;
        color: white;
        margin-bottom: 10px;
        text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
      }

      .logo span {
        color: var(--success);
      }

      .card {
        background: white;
        border-radius: 20px;
        padding: 40px;
        box-shadow: var(--shadow-lg);
        border: 1px solid rgba(255,255,255,0.2);
      }

      .card-header {
        text-align: center;
        margin-bottom: 30px;
      }

      .card-title {
        font-size: 2rem;
        font-weight: 700;
        color: var(--dark);
        margin-bottom: 10px;
      }

      .card-subtitle {
        color: var(--gray);
        font-size: 1.1rem;
      }

      .ad-info {
        background: var(--light);
        border-radius: 15px;
        padding: 20px;
        margin-bottom: 25px;
        border-left: 4px solid var(--primary);
      }

      .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 15px;
        margin-top: 15px;
      }

      .info-item {
        display: flex;
        flex-direction: column;
      }

      .info-label {
        font-weight: 600;
        color: var(--dark);
        font-size: 0.9rem;
      }

      .info-value {
        color: var(--gray);
        font-size: 1rem;
      }

      .status-badge {
        display: inline-block;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 0.8rem;
        font-weight: 600;
      }

      .status-draft {
        background: #fff3cd;
        color: #856404;
      }

      .status-moderation {
        background: #cce7ff;
        color: #004085;
      }

      .status-active {
        background: #d4edda;
        color: #155724;
      }

      .form-section {
        margin-bottom: 30px;
        padding: 25px;
        background: var(--light);
        border-radius: 15px;
        border-left: 4px solid var(--warning);
      }

      .section-title {
        font-size: 1.3rem;
        font-weight: 600;
        color: var(--dark);
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .form-group {
        margin-bottom: 20px;
      }

      label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: var(--dark);
      }

      .required::after {
        content: " *";
        color: var(--danger);
      }

      .form-control {
        width: 100%;
        padding: 12px 16px;
        border: 2px solid var(--border);
        border-radius: 10px;
        font-size: 1rem;
        transition: all 0.3s ease;
        background: white;
      }

      .form-control:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
      }

      textarea.form-control {
        min-height: 120px;
        resize: vertical;
      }

      .radio-group {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        margin-top: 10px;
      }

      .radio-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 20px;
        background: white;
        border: 2px solid var(--border);
        border-radius: 10px;
        cursor: pointer;
        transition: all 0.3s ease;
        flex: 1;
        min-width: 120px;
      }

      .radio-item:hover {
        border-color: var(--primary);
      }

      .radio-item input[type="radio"] {
        margin: 0;
      }

      .radio-label {
        font-weight: 500;
        color: var(--dark);
      }

      .btn {
        padding: 12px 24px;
        border: none;
        border-radius: 10px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
      }

      .btn-primary {
        background: linear-gradient(135deg, var(--primary), var(--secondary));
        color: white;
      }

      .btn-primary:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(67, 97, 238, 0.3);
      }

      .btn-outline {
        background: transparent;
        color: var(--gray);
        border: 2px solid var(--border);
      }

      .btn-outline:hover {
        border-color: var(--primary);
        color: var(--primary);
      }

      .btn-danger {
        background: var(--danger);
        color: white;
      }

      .btn-danger:hover {
        background: #e00;
        transform: translateY(-2px);
      }

      .form-actions {
        display: flex;
        gap: 15px;
        margin-top: 30px;
        flex-wrap: wrap;
      }

      .alert {
        padding: 15px 20px;
        border-radius: 10px;
        margin-bottom: 25px;
        font-weight: 500;
      }

      .alert-error {
        background: rgba(247, 37, 133, 0.1);
        border: 1px solid var(--danger);
        color: var(--danger);
      }

      .alert-success {
        background: rgba(76, 201, 240, 0.1);
        border: 1px solid var(--success);
        color: var(--success);
      }

      .tags-hint {
        font-size: 0.9rem;
        color: var(--gray);
        margin-top: 5px;
      }

      .edit-note {
        background: #fff3cd;
        border: 1px solid #ffeaa7;
        color: #856404;
        padding: 15px;
        border-radius: 10px;
        margin-bottom: 20px;
        border-left: 4px solid var(--warning);
      }

      /* Адаптивность */
      @media (max-width: 768px) {
        body {
          padding: 10px;
        }

        .card {
          padding: 25px 20px;
        }

        .form-section {
          padding: 20px 15px;
        }

        .radio-group {
          flex-direction: column;
        }

        .form-actions {
          flex-direction: column;
        }

        .info-grid {
          grid-template-columns: 1fr;
        }
      }

      .icon {
        display: inline-block;
        width: 24px;
        height: 24px;
        text-align: center;
        line-height: 24px;
      }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="logo">Portal</div>
    </div>

    <div class="card">
        <div class="card-header">
            <h1 class="card-title">Редактировать объявление</h1>
            <p class="card-subtitle">ID: #<%= announcement.getId() %></p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">
            <span class="icon">⚠</span> <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success">
            <span class="icon">✓</span> <%= request.getAttribute("success") %>
        </div>
        <% } %>

        <!-- Информация об объявлении -->
        <div class="ad-info">
            <h3 class="section-title">
                <span class="icon">📊</span> Текущие данные
            </h3>
            <div class="info-grid">
                <div class="info-item">
                    <span class="info-label">Статус:</span>
                    <%
                        String statusClass = "";
                        String statusText = announcement.getStatus().getDisplayName();

                        switch (announcement.getStatus()) {
                            case DRAFT:
                                statusClass = "status-draft";
                                break;
                            case UNDER_MODERATION:
                                statusClass = "status-moderation";
                                break;
                            case ACTIVE:
                                statusClass = "status-active";
                                break;
                            default:
                                statusClass = "status-draft";
                        }
                    %>
                    <span class="status-badge <%= statusClass %>"><%= statusText %></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Просмотры:</span>
                    <span class="info-value"><%= announcement.getViewCount() != null ? announcement.getViewCount() : 0 %></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Создано:</span>
                    <span class="info-value"><%= announcement.getCreatedAt() != null ? announcement.getCreatedAt() : "Не указано" %></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Обновлено:</span>
                    <span class="info-value"><%= announcement.getUpdatedAt() != null ? announcement.getUpdatedAt() : "Не указано" %></span>
                </div>
            </div>
        </div>

        <div class="edit-note">
            <strong>💡 Примечание:</strong>
            <% if (!announcement.canBeEdited()) { %>
            Это объявление нельзя редактировать в текущем статусе. Сначала измените статус на "Черновик".
            <% } else { %>
            Вы можете редактировать все поля объявления. После сохранения статус может измениться.
            <% } %>
        </div>

        <!-- Форма редактирования -->
        <form id="editAdForm" action="edit-ad" method="post" enctype="multipart/form-data" onsubmit="return false;">
            <input type="hidden" name="adId" value="<%= announcement.getId() %>">

            <!-- Основная информация -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📝</span> Основная информация
                </h3>

                <div class="form-group">
                    <label for="title" class="required">Заголовок объявления</label>
                    <input type="text" id="title" name="title" class="form-control"
                           placeholder="Например: iPhone 13 Pro Max 256GB" required
                           value="<%= announcement.getTitle() != null ? announcement.getTitle() : "" %>"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                </div>

                <div class="form-group">
                    <label for="description" class="required">Описание</label>
                    <textarea id="description" name="description" class="form-control"
                              placeholder="Подробно опишите ваш товар или услугу..." required
                            <%= !announcement.canBeEdited() ? "disabled" : "" %>><%= announcement.getDescription() != null ? announcement.getDescription() : "" %></textarea>
                </div>
            </div>

            <!-- Категории -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📂</span> Категория
                </h3>

                <div class="form-group">
                    <label for="category" class="required">Основная категория</label>
                    <select id="category" name="category" class="form-control" required
                            <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                        <option value="">Выберите категорию</option>
                        <% for (Category category : Category.values()) { %>
                        <option value="<%= category.name() %>"
                                <%= announcement.getCategory() == category ? "selected" : "" %>>
                            <%= category.getDisplayName() %>
                        </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="subcategory">Подкатегория</label>
                    <input type="text" id="subcategory" name="subcategory" class="form-control"
                           placeholder="Например: Смартфоны и телефоны"
                           value="<%= announcement.getSubcategory() != null ? announcement.getSubcategory() : "" %>"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                </div>
            </div>

            <!-- Местоположение и состояние -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📍</span> Местоположение и состояние
                </h3>

                <div class="form-group">
                    <label for="location" class="required">Местоположение</label>
                    <input type="text" id="location" name="location" class="form-control"
                           placeholder="Например: Москва, центр" required
                           value="<%= announcement.getLocation() != null ? announcement.getLocation() : "" %>"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                </div>

                <div class="form-group">
                    <label class="required">Состояние товара</label>
                    <div class="radio-group">
                        <% for (Condition condition : Condition.values()) { %>
                        <label class="radio-item">
                            <input type="radio" name="condition"
                                   value="<%= condition.name() %>" required
                                <%= announcement.getCondition() == condition ? "checked" : "" %>
                                <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                            <span class="radio-label"><%= condition.getDisplayName() %></span>
                        </label>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- Цена -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">💰</span> Цена
                </h3>

                <div class="form-group">
                    <label class="required">Тип цены</label>
                    <div class="radio-group">
                        <%
                            int price = announcement.getPrice();
                            String priceType = price == -1 ? "negotiable" : price == 0 ? "free" : "fixed";
                        %>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="negotiable"
                                <%= "negotiable".equals(priceType) ? "checked" : "" %>
                                <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                            <span class="radio-label">Договорная</span>
                        </label>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="free"
                                <%= "free".equals(priceType) ? "checked" : "" %>
                                <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                            <span class="radio-label">Бесплатно</span>
                        </label>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="fixed"
                                <%= "fixed".equals(priceType) ? "checked" : "" %>
                                <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                            <span class="radio-label">Указать цену</span>
                        </label>
                    </div>
                </div>

                <div class="form-group">
                    <label for="price">Цена (руб.)</label>
                    <input type="number" id="price" name="price" class="form-control"
                           min="1" max="1000000000" placeholder="1000"
                           value="<%= price > 0 ? price : "" %>"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                    <div class="tags-hint">
                        <strong>Напишите цену, если выбрали пункт "Указать цену"</strong>
                    </div>
                </div>
            </div>

            <!-- Фотографии -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📷</span> Фотографии
                </h3>

                <div class="form-group">
                    <label for="photos">Изменить фотографии</label>
                    <input type="file" id="photos" name="photos" class="form-control"
                           multiple accept="image/*"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                    <div class="tags-hint">
                        Выберите новые фотографии. Если не выберете, существующие фотографии останутся без изменений.
                    </div>
                    <div class="tags-hint" style="margin-top: 10px;">
                        <strong>Примечание:</strong> При загрузке новых фотографий старые будут заменены.
                    </div>
                </div>
            </div>

            <!-- Теги -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">🏷️</span> Теги
                </h3>

                <div class="form-group">
                    <label for="tags">Ключевые слова</label>
                    <input type="text" id="tags" name="tags" class="form-control"
                           placeholder="например: электроника, б/у, срочно, apple"
                           value="<%= announcement.getTags() != null ? String.join(", ", announcement.getTags()) : "" %>"
                        <%= !announcement.canBeEdited() ? "disabled" : "" %>>
                    <div class="tags-hint">Введите теги через запятую для лучшего поиска</div>
                </div>
            </div>

            <!-- Действие после сохранения -->
            <% if (announcement.canBeEdited()) { %>
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">⚡</span> Действие после сохранения
                </h3>

                <div class="radio-group">
                    <label class="radio-item">
                        <input type="radio" name="action" value="draft" checked>
                        <span class="radio-label">Сохранить как черновик</span>
                    </label>
                    <label class="radio-item">
                        <input type="radio" name="action" value="publish">
                        <span class="radio-label">Опубликовать (отправить на модерацию)</span>
                    </label>
                </div>
            </div>
            <% } %>

            <!-- Кнопки действий -->
            <div class="form-actions">
                <a href="dashboard.jsp" class="btn btn-outline">
                    <span class="icon">←</span> Назад к списку
                </a>

                <% if (announcement.canBeEdited()) { %>
                <button type="submit" class="btn btn-primary" onclick="submitEditAdForm()">
                    <span class="icon">💾</span> Сохранить изменения
                </button>
                <% } else { %>
                <a href="edit-ad?action=toDraft&adId=<%= announcement.getId() %>" class="btn btn-warning">
                    <span class="icon">📝</span> Сделать черновиком
                </a>
                <% } %>

                <a href="delete-ad?adId=<%= announcement.getId() %>" class="btn btn-danger"
                   onclick="return confirm('Вы уверены, что хотите удалить это объявление?')">
                    <span class="icon">🗑️</span> Удалить
                </a>
            </div>
        </form>
    </div>
</div>

<%@ include file="profanity-check.jsp" %>
<script>
    function submitEditAdForm() {
        validateFormWithProfanity('editAdForm', ['title', 'description', 'subcategory', 'location', 'tags'], function() {
            document.getElementById('editAdForm').submit();
        });
    }
</script>
</body>
</html>