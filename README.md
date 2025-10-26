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
качаем драйвер
``` 
wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```
поднимаем докер
```
docker-compose up -d
docker ps
```
компилим проектик
```
mvn clean compile
```
запускаем проектик
```
java -cp "target/classes:postgresql-42.6.0.jar" com.mipt.portal.Main
```
