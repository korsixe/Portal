-- Создание таблицы пользователей
CREATE TABLE Users (
                       email TEXT NOT NULL UNIQUE,              -- Электронная почта (уникальная)
                       name TEXT NOT NULL,                       -- Имя пользователя
                       password TEXT NOT NULL,                   -- Пароль
                       address TEXT,                             -- Адрес (может быть пустым)
                       study_program TEXT,                       -- Учебная программа
                       course INTEGER,                           -- Курс
                       rating REAL DEFAULT 0.0,                  -- Рейтинг (по умолчанию 0.0)
                       coins INTEGER DEFAULT 0                   -- Количество монет (по умолчанию 0)
);

-- Создание таблицы объявлений
CREATE TABLE Ads (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,     -- Уникальный идентификатор объявления
                     title TEXT NOT NULL,                       -- Заголовок
                     description TEXT,                          -- Описание
                     category INTEGER,                          -- Категория
                     condition TEXT,                            -- Состояние
                     price INTEGER NOT NULL,                    -- Цена
                     location TEXT,                             -- Местоположение
                     email TEXT NOT NULL,                       -- Электронная почта
                     status TEXT NOT NULL,                      -- Статус (Активно, Архив, Черновик)
                     FOREIGN KEY (email) REFERENCES Users(email) -- Связь с таблицей Users
);
