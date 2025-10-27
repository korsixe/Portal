# Portal
Проект "Portal" - проект для улучшения жизни студентов МФТИ

# Компиляция и Запуск
```
mvn clean install
```
```
java -cp target/classes com.mipt.portal.testing_user.Application
```

# Запуск БД с драйвером
``` 
wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```
```
docker-compose up -d
```
```
mvn clean compile
```
```
java -cp "target/classes:postgresql-42.6.0.jar" com.mipt.portal.Main
```
