-- Условно создали таблицу, но я пока не понимаю, как создать и использовать ее

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,                    -- Используем BIGSERIAL для автоинкремента
    email TEXT NOT NULL UNIQUE,                  -- Электронная почта (уникальная)
    name TEXT NOT NULL,                          -- Имя пользователя
    password TEXT NOT NULL,                      -- Пароль
    address TEXT,                                -- Адрес (может быть пустым)
    study_program TEXT,                          -- Учебная программа
    course INTEGER,                              -- Курс
    rating REAL DEFAULT 0.0,                     -- Рейтинг (по умолчанию 0.0)
    coins INTEGER DEFAULT 0,                     -- Количество монет (по умолчанию 0)
    ad_list BIGINT[] DEFAULT '{}'                -- Список ID объявлений (массив BIGINT)
);
-- а как создать зависимоть для user --> ads

-- Создание таблицы объявлений
CREATE TABLE IF NOT EXISTS ads (
    id BIGINT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    category INTEGER NOT NULL,                      -- Категория (храним как число: 0=ELECTRONICS, 1=CLOTHING, и т.д.)
    condition INTEGER NOT NULL,                     -- Состояние (храним как число: 0=USED, 1=NEW, 2=BROKEN)
    price INTEGER NOT NULL,
    location TEXT,
    user_id BIGINT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    view_count INTEGER DEFAULT 0,
    photo_urls BYTEA NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

