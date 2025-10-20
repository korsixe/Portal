CREATE TABLE Ads (
                     id SERIAL PRIMARY KEY,                   -- Уникальный идентификатор для каждой записи
                     title VARCHAR(255) NOT NULL,            -- Заголовок объявления
                     description TEXT NOT NULL,               -- Описание объявления
                     category INT NOT NULL,                   -- Категория (в будущем может быть связано с другой таблицей)
                     condition VARCHAR(100) NOT NULL,        -- Состояние
                     negotiable_price BOOLEAN NOT NULL,      -- Договорная цена
                     price INT NOT NULL,                      -- Цена
                     location VARCHAR(255) NOT NULL,         -- Местоположение
                     id_user VARCHAR(255) NOT NULL,          -- Почта пользователя
                     status VARCHAR(100) NOT NULL             -- Статус объявления
);
