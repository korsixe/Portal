DELETE FROM ads;

INSERT INTO ads (title, description, category, condition, price, location, user_id, status, view_count)
VALUES
    ('Продам MacBook Pro 2022', 'Отличное состояние, батарея 95%, 16ГБ ОЗУ', 0, 1, 150000, 'Долгопрудный',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 15),

    ('Отдам книги по программированию', 'CLRS, Таненбаум, бесплатно', 3, 1, 0, 'МФТИ',
     (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE', 31);