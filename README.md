# Portal
Проект "Portal" - проект для улучшения жизни студентов МФТИ

### Запуск проекта
1. Запустите Базу Данных
```
docker-compose up -d
```
2. Заполните Базу Данных тестовыми данными
```
mvn compile exec:java -Dexec.mainClass="com.mipt.portal.database.TestData"
```
3. Запустите весь проект
```
mvn jetty:run
```
4. Перейдите на наш сайт
```
http://localhost:8080/portal/ #Вход для пользователей
```
```
http://localhost:8080/portal/moderator/login-moderator.jsp  #Вход для модераторов
```
