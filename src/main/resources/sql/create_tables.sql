DROP TABLE IF EXISTS ads CASCADE;

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY, -- Используем BIGSERIAL для автоинкремента
    email         TEXT NOT NULL UNIQUE,  -- Электронная почта (уникальная)
    name          TEXT NOT NULL,         -- Имя пользователя
    password      TEXT NOT NULL,         -- Пароль
    address       TEXT,                  -- Адрес (может быть пустым)
    study_program TEXT,                  -- Учебная программа
    course        INTEGER,               -- Курс
    rating        REAL     DEFAULT 0.0,  -- Рейтинг (по умолчанию 0.0)
    coins         INTEGER  DEFAULT 0,    -- Количество монет (по умолчанию 0)
    ad_list       BIGINT[] DEFAULT '{}'  -- Список ID объявлений (массив BIGINT)
);
-- а как создать зависимоть для user --> ads

CREATE TABLE IF NOT EXISTS ads
(
    id          BIGSERIAL PRIMARY KEY,
    title       TEXT                     NOT NULL,
    description TEXT,
    category    INTEGER                  NOT NULL,
    subcategory TEXT,
    condition   INTEGER                  NOT NULL,
    price       INTEGER                  NOT NULL,
    location    TEXT,
    user_id     BIGINT                   NOT NULL,
    status      TEXT                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    view_count  INTEGER                           DEFAULT 0,
    tags        JSONB,
    tags_count  INTEGER                           DEFAULT 0,
    photos      BYTEA[]                          DEFAULT '{}', -- массив бинарных данных фотографий
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS moderators
(
    id          SERIAL PRIMARY KEY,
    email       TEXT UNIQUE NOT NULL,
    name        TEXT        NOT NULL,
    password    TEXT        NOT NULL,
    permissions TEXT[]    DEFAULT '{}'
);

-- Создание таблицы комментариев
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGSERIAL PRIMARY KEY,
                                        ad_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        user_name TEXT NOT NULL,
                                        content TEXT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                        FOREIGN KEY (ad_id) REFERENCES ads(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица для сообщений модераторов
CREATE TABLE IF NOT EXISTS moderation_messages (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   ad_id BIGINT NOT NULL,
                                                   moderator_email TEXT NOT NULL,
                                                   action TEXT NOT NULL, -- 'approve', 'reject', 'delete'
                                                   reason TEXT, -- причина модерации
                                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                                                   FOREIGN KEY (ad_id) REFERENCES ads(id) ON DELETE CASCADE
);