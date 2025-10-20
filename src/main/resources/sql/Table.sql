-- Примерный вид таблиц в БД

CREATE TABLE User (
                      email TEXT PRIMARY KEY,                   -- Электронная почта пользователя (уникальная и первичный ключ)
                      name TEXT NOT NULL,                       -- Имя пользователя
                      password TEXT NOT NULL,                   -- Пароль пользователя
                      address TEXT,                             -- Адрес пользователя (может быть NULL)
                      studyProgram TEXT,                        -- Учебная программа
                      course INTEGER,                           -- Номер курса
                      rating REAL DEFAULT 0,                    -- Рейтинг пользователя (по умолчанию 0)
                      coins INTEGER DEFAULT 0                   -- Количество монет (по умолчанию 0)
);

CREATE TABLE Ads (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,     -- Уникальный идентификатор для каждой записи
                     title TEXT NOT NULL,                      -- Заголовок объявления
                     description TEXT NOT NULL,                -- Описание объявления
                     category INTEGER NOT NULL,                -- Категория
                     condition TEXT NOT NULL,                  -- Состояние
                     negotiable_price INTEGER NOT NULL,        -- Договорная цена
                     price INTEGER NOT NULL,                   -- Цена
                     location TEXT NOT NULL,                   -- Местоположение
                     userEmail TEXT NOT NULL,                 -- Внешний ключ для связи с пользователем
                     status TEXT NOT NULL,                     -- Статус объявления
                     FOREIGN KEY (userEmail) REFERENCES User(email) -- Связь с таблицей User
);
