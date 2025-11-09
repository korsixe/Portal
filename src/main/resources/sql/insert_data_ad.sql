-- Вставка тестовых данных
INSERT INTO ads (title, description, category, condition, price, location, user_id, status, view_count, tags, tags_count)
VALUES
    ('Продам MacBook Pro 2022', 'Отличное состояние, батарея 95%, 16ГБ ОЗУ, 512ГБ SSD', 0, 1, 150000, 'Долгопрудный',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 15,
     '["ноутбук", "apple", "техника", "macbook"]'::JSONB, 4),

    ('Отдам книги по программированию', 'CLRS, Таненбаум, бесплатно. Алгоритмы и компьютерные сети', 3, 1, 0, 'МФТИ',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 31,
     '["книги", "программирование", "алгоритмы", "бесплатно"]'::JSONB, 4),

    -- Объявления на модерации
    ('Продам iPhone 13', '128GB, черный, гарантия до 2024 года', 0, 0, 60000, 'Москва',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'UNDER_MODERATION', 5,
     '["iphone", "смартфон", "apple", "техника"]'::JSONB, 4),

    ('Сниму квартиру у МФТИ', 'Ищу 1-2 комнатную квартиру для студента, долгосрочно', 2, 1, 25000, 'Долгопрудный',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'UNDER_MODERATION', 12,
     '["аренда", "квартира", "долгосрочно", "студент"]'::JSONB, 4),

    ('Репетитор по математике', 'Подготовка к ЕГЭ и олимпиадам, опыт 5 лет', 4, 1, 1500, 'Онлайн',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'UNDER_MODERATION', 8,
     '["репетитор", "математика", "егэ", "олимпиады"]'::JSONB, 4),

    ('Куплю велосипед горный', 'Ищу недорогой горный велосипед в хорошем состоянии', 1, 1, 10000, 'Долгопрудный',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'UNDER_MODERATION', 3,
     '["куплю", "велосипед", "горный", "спорт"]'::JSONB, 4);