-- Создание таблицы категорий и подкатегорий
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            parent_id INTEGER REFERENCES categories(id),
                            is_service BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы тегов
CREATE TABLE tags (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(50) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы значений тегов
CREATE TABLE tag_values (
                            id SERIAL PRIMARY KEY,
                            tag_id INTEGER REFERENCES tags(id) ON DELETE CASCADE,
                            value VARCHAR(100) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
