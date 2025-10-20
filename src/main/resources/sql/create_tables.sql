-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name TEXT NOT NULL,
                                     email TEXT NOT NULL UNIQUE,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы объявлений
CREATE TABLE IF NOT EXISTS ads (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   title TEXT NOT NULL,
                                   description TEXT,
                                   price DECIMAL(10, 2) NOT NULL,
                                   user_id INTEGER NOT NULL,
                                   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Создание индексов для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_ads_title ON ads (title);
CREATE INDEX IF NOT EXISTS idx_ads_price ON ads (price);
CREATE INDEX IF NOT EXISTS idx_ads_created_at ON ads (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

-- Индекс для полнотекстового поиска (если понадобится)
CREATE INDEX IF NOT EXISTS idx_ads_search ON ads (title, description);