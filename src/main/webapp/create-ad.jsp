<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.announcement.Category" %>
<%@ page import="com.mipt.portal.announcement.Condition" %>
<html>
<head>
    <title>Создать объявление</title>
    <style>
      body { font-family: Arial, sans-serif; margin: 40px; }
      .form-group { margin-bottom: 15px; }
      label { display: block; margin-bottom: 5px; font-weight: bold; }
      input, select, textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
      textarea { height: 100px; }
      .btn { background: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
      .price-section { display: none; }
      .error { color: red; margin-bottom: 15px; }
    </style>
    <script>
      function togglePriceInput() {
        var priceType = document.querySelector('input[name="priceType"]:checked').value;
        var priceSection = document.getElementById('priceSection');

        if (priceType === 'fixed') {
          priceSection.style.display = 'block';
        } else {
          priceSection.style.display = 'none';
        }
      }
    </script>
</head>
<body>
<h1>Создать новое объявление</h1>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>

<form action="create-ad" method="post">
    <div class="form-group">
        <label for="title">Заголовок *</label>
        <input type="text" id="title" name="title" required>
    </div>

    <div class="form-group">
        <label for="description">Описание *</label>
        <textarea id="description" name="description" required></textarea>
    </div>

    <div class="form-group">
        <label for="category">Категория *</label>
        <select id="category" name="category" required>
            <option value="">Выберите категорию</option>
            <% for (Category category : Category.values()) { %>
            <option value="<%= category.name() %>"><%= category.getDisplayName() %></option>
            <% } %>
        </select>
    </div>

    <div class="form-group">
        <label for="location">Местоположение *</label>
        <input type="text" id="location" name="location" required>
    </div>

    <div class="form-group">
        <label>Состояние товара *</label>
        <% for (Condition condition : Condition.values()) { %>
        <div>
            <input type="radio" id="<%= condition.name() %>" name="condition" value="<%= condition.name() %>" required>
            <label for="<%= condition.name() %>" style="display: inline;"><%= condition.getDisplayName() %></label>
        </div>
        <% } %>
    </div>

    <div class="form-group">
        <label>Тип цены *</label>
        <div>
            <input type="radio" name="priceType" value="negotiable" checked onchange="togglePriceInput()">
            <label style="display: inline;">Договорная</label>
        </div>
        <div>
            <input type="radio" name="priceType" value="free" onchange="togglePriceInput()">
            <label style="display: inline;">Бесплатно</label>
        </div>
        <div>
            <input type="radio" name="priceType" value="fixed" onchange="togglePriceInput()">
            <label style="display: inline;">Указать цену</label>
        </div>
    </div>

    <div id="priceSection" class="form-group price-section">
        <label for="price">Цена (руб.)</label>
        <input type="number" id="price" name="price" min="1" max="1000000000">
    </div>

    <div class="form-group">
        <label>Действие после создания</label>
        <div>
            <input type="radio" name="action" value="draft" checked>
            <label style="display: inline;">Сохранить как черновик</label>
        </div>
        <div>
            <input type="radio" name="action" value="publish">
            <label style="display: inline;">Опубликовать (отправить на модерацию)</label>
        </div>
    </div>

    <button type="submit" class="btn">Создать объявление</button>
    <a href="index.jsp" style="margin-left: 10px;">Отмена</a>
</form>
</body>
</html>