# Portal
Portal — это единая цифровая экосистема, созданная для решения ключевых бытовых проблем студентов МФТИ. Проект объединяет три основных функциональных блока:

1. **Маркетплейс** — безопасная платформа для покупки и продажи б/у вещей исключительно среди студентов

2. **Карта аренды** — система поиска и бронирования доступных инструментов, книг и техники

3. **Афиша событий** — платформа для анонсирования и поиска мероприятий внутри общежитий

Проект реализуется в рамках программы "Высшая школа программной инженерии" МФТИ при поддержке компании ООО MWS.
## Ментор проекта
  ФИО: Бобряков Д.С.
  
  Контакты: @DmitryBobryakov
  
## Архитектура
Проект построен по классической трехзвенной архитектуре:

**Презентационный слой:** JSP-страницы с HTML/CSS

**Бизнес-логика:** Java-сервлеты, обрабатывающие запросы пользователей

**Слой данных:** PostgreSQL база данных, управляемая через JDBC

## Технологический стек
**Backend:** Java, Java Servlets, JSP

**Frontend:** HTML, CSS, JSP

**База данных:** PostgreSQL

**Сборка и запуск:** Maven, Jetty

**Контейнеризация:** Docker, Docker Compose

**Система контроля версий:** Git

## Конфигурации
### Настройки базы данных (docker-compose.yml)
```
services:
  postgres:
    image: postgres:15
    container_name: postgres-db
    environment:
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
      POSTGRES_DB: myproject
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  postgres_data:
```
### Настройки подключения к БД
URL: ```jdbc:postgresql://localhost:5433/myproject```

Пользователь: ```myuser```

Пароль: ```mypassword```

Драйвер: ```org.postgresql.Driver```

## Предварительные требования
1. Java Development Kit (JDK) 8+
2. Apache Maven 3.6+
3. Docker и Docker Compose
4. Git

Рекомендуемая ОС: Linux, macOS

## Запуск проекта
1. Клонирование репозитория
```
git clone <repository-url>
cd portal
```
2. Запуск базы данных
```
docker-compose up -d
```
3. Инициализация базы данных тестовыми данными
```
mvn compile exec:java -Dexec.mainClass="com.mipt.portal.database.TestData"
```
4. Сборка и запуск приложения
```
mvn jetty:run
```
5. Доступ к приложению
После успешного запуска приложение будет доступно по следующим адресам:

  Для пользователей: ```http://localhost:8080/portal/```

  Для модераторов: ```http://localhost:8080/portal/moderator/login-moderator.jsp```

