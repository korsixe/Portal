-- Условно создали таблицу, но я пока не понимаю, как создать и использовать ее

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
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category INTEGER NOT NULL,                      -- Категория (храним как число: 0=ELECTRONICS, 1=CLOTHING, и т.д.)
    condition INTEGER NOT NULL,                     -- Состояние (храним как число: 0=USED, 1=NEW, 2=BROKEN)
    price INTEGER NOT NULL,
    location TEXT,
    email TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    view_count INTEGER DEFAULT 0,
    FOREIGN KEY (email) REFERENCES Users(email)
);