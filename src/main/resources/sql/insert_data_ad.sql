-- Вставка тестовых данных
INSERT INTO ads (title, description, category, condition, price, location, user_id, status, view_count, tags, tags_count)
VALUES
    ('Продам MacBook Pro 2022', 'Отличное состояние, батарея 95%, 16ГБ ОЗУ, 512ГБ SSD', 0, 1, 150000, 'Долгопрудный',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 15,
     '["ноутбук", "apple", "техника", "macbook"]'::JSONB, 4),

    ('Отдам книги по программированию', 'CLRS, Таненбаум, бесплатно. Алгоритмы и компьютерные сети', 3, 1, 0, 'МФТИ',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 31,
     '["книги", "программирование", "алгоритмы", "бесплатно"]'::JSONB, 4);

-- Вставка фотографий для объявлений