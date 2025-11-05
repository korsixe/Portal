-- Создание таблицы комментариев
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGSERIAL PRIMARY KEY,                    -- Уникальный ID
                                        ad_id BIGINT NOT NULL,                       -- ID объявления
                                        user_id BIGINT NOT NULL,                     -- ID пользователя
                                        content TEXT NOT NULL,                       -- Текст комментария
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания

    FOREIGN KEY (ad_id) REFERENCES ads(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );