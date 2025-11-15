<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.announcement.Category" %>
<%@ page import="com.mipt.portal.announcement.Condition" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Обработка отображения поля цены на сервере
    String priceType = request.getParameter("priceType");
    boolean showPrice = "fixed".equals(priceType);
    if (priceType == null) {
        priceType = "negotiable"; // значение по умолчанию
    }
%>
<html>
<head>
    <title>Создать объявление • Portal</title>
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
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
      }

      .logo span {
        color: var(--success);
      }

      .card {
        background: white;
        border-radius: 20px;
        padding: 40px;
        box-shadow: var(--shadow-lg);
        border: 1px solid rgba(255, 255, 255, 0.2);
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

      .form-section {
        margin-bottom: 30px;
        padding: 25px;
        background: var(--light);
        border-radius: 15px;
        border-left: 4px solid var(--primary);
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

      .price-section {
        margin-top: 15px;
        padding: 15px;
        background: white;
        border-radius: 10px;
        border: 2px solid var(--primary);
        animation: fadeIn 0.5s ease;
      }

      @keyframes fadeIn {
        from {
          opacity: 0;
          transform: translateY(-10px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
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

      .btn-preview {
        background: var(--warning);
        color: white;
      }

      .btn-preview:hover {
        background: #e68900;
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

      .alert-info {
        background: rgba(67, 97, 238, 0.1);
        border: 1px solid var(--primary);
        color: var(--primary);
      }

      .tags-hint {
        font-size: 0.9rem;
        color: var(--gray);
        margin-top: 5px;
      }

      .preview-note {
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
            <h1 class="card-title">Создать новое объявление</h1>
            <p class="card-subtitle">Заполните информацию о вашем товаре или услуге</p>
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

        <!-- Основная форма для создания объявления -->
        <form action="create-ad" method="post" enctype="multipart/form-data">
            <!-- Основная информация -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📝</span> Основная информация
                </h3>

                <div class="form-group">
                    <label for="title" class="required">Заголовок объявления</label>
                    <input type="text" id="title" name="title" class="form-control"
                           placeholder="Например: iPhone 13 Pro Max 256GB" required
                           value="<%= request.getParameter("title") != null ? request.getParameter("title") : "" %>">
                </div>

                <div class="form-group">
                    <label for="description" class="required">Описание</label>
                    <textarea id="description" name="description" class="form-control"
                              placeholder="Подробно опишите ваш товар или услугу..." required><%=
                    request.getParameter("description") != null ? request.getParameter(
                            "description") : "" %></textarea>
                </div>
            </div>

            <!-- Категории -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">📂</span> Категория
                </h3>

                <div class="form-group">
                    <label for="category" class="required">Основная категория</label>
                    <select id="category" name="category" class="form-control" required>
                        <option value="">Выберите категорию</option>
                        <% for (Category category : Category.values()) { %>
                        <option value="<%= category.name() %>"
                                <%=
                                (request.getParameter("category") != null && request.getParameter(
                                        "category").equals(category.name())) ? "selected" : "" %>>
                            <%= category.getDisplayName() %>
                        </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="subcategory">Подкатегория</label>
                    <input type="text" id="subcategory" name="subcategory" class="form-control"
                           placeholder="Например: Смартфоны и телефоны"
                           value="<%= request.getParameter("subcategory") != null ? request.getParameter("subcategory") : "" %>">
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
                           value="<%= request.getParameter("location") != null ? request.getParameter("location") : "" %>">
                </div>

                <div class="form-group">
                    <label class="required">Состояние товара</label>
                    <div class="radio-group">
                        <% for (Condition condition : Condition.values()) { %>
                        <label class="radio-item">
                            <input type="radio" name="condition"
                                   value="<%= condition.name() %>" required
                                <%= (request.getParameter("condition") != null && request.getParameter("condition").equals(condition.name())) ? "checked" : "" %>>
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
                            String currentPriceType = request.getParameter("priceType");
                            if (currentPriceType == null) {
                                currentPriceType = "negotiable";
                            }
                        %>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="negotiable"
                                <%= "negotiable".equals(currentPriceType) ? "checked" : "" %>>
                            <span class="radio-label">Договорная</span>
                        </label>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="free"
                                <%= "free".equals(currentPriceType) ? "checked" : "" %>>
                            <span class="radio-label">Бесплатно</span>
                        </label>
                        <label class="radio-item">
                            <input type="radio" name="priceType" value="fixed"
                                <%= "fixed".equals(currentPriceType) ? "checked" : "" %>>
                            <span class="radio-label">Указать цену</span>
                        </label>
                    </div>
                </div>

                <div class="form-group">
                    <label for="price">Цена (руб.)</label>
                    <input type="number" id="price" name="price" class="form-control"
                           min="1" max="1000000000" placeholder="1000"
                           value="<%= request.getParameter("price") != null ? request.getParameter("price") : "" %>">
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
                    <label for="photos">Добавить фотографии</label>
                    <input type="file" id="photos" name="photos" class="form-control"
                           multiple accept="image/*">
                    <div class="tags-hint">Можно выбрать несколько файлов (JPEG, PNG, GIF)</div>
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
                           value="<%= request.getParameter("tags") != null ? request.getParameter("tags") : "" %>">
                    <div class="tags-hint">Введите теги через запятую для лучшего поиска</div>
                </div>
            </div>

            <!-- Действие после создания -->
            <div class="form-section">
                <h3 class="section-title">
                    <span class="icon">⚡</span> Действие после создания
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

            <!-- Кнопки действий -->
            <div class="form-actions">
                <a href="dashboard.jsp" class="btn btn-outline">
                    <span class="icon">←</span> Отмена
                </a>

                <button type="submit" class="btn btn-primary">
                    <span class="icon">✓</span> Создать объявление
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>