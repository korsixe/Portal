-- Вставка тестовых объявлений

-- Анастасия Шабунина (shabunina.ao@phystech.edu)
INSERT INTO ads (title, description, category, condition, price, location, user_id, status) VALUES
                                                                                                ('MacBook Pro 13" 2020', 'Отличный MacBook в идеальном состоянии', 'ELECTRONICS', 'NEW', 75000, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE'),
                                                                                                ('Учебник по матану', 'Сборник задач за 1 курс', 'BOOKS', 'NEW', 500, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'ACTIVE'),
                                                                                                ('Настольная лампа', 'Светодиодная лампа с регулировкой', 'ELECTRONICS', 'USED', 1200, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'DRAFT'),
                                                                                                ('Калькулятор Casio', 'Инженерный калькулятор', 'ELECTRONICS', 'BROKEN', 800, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'shabunina.ao@phystech.edu'), 'UNDER_MODERATION');

-- Мария Соколова (ivanov.ii@phystech.edu)
INSERT INTO ads (title, description, category, condition, price, location, user_id, status) VALUES
                                                                                                ('Учебник по физике', 'Курс общей физики Ландсберга', 'BOOKS', 'USED', 1500, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'ivanov.ii@phystech.edu'), 'ACTIVE'),
                                                                                                ('Микроскоп школьный', 'Детский микроскоп для начинающих', 'CHILDREN', 'USED', 2000, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'ivanov.ii@phystech.edu'), 'ACTIVE'),
                                                                                                ('Рюкзак студенческий', 'Вместительный рюкзак для ноутбука', 'OTHER', 'USED', 800, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'ivanov.ii@phystech.edu'), 'DRAFT');

-- Дмитрий Орлов (orlov.ka@phystech.edu)
INSERT INTO ads (title, description, category, condition, price, location, user_id, status) VALUES
                                                                                                ('Игровой компьютер', 'Gaming PC для учебы и игр', 'ELECTRONICS', 'USED', 45000, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'orlov.ka@phystech.edu'), 'ACTIVE'),
                                                                                                ('Клавиатура механическая', 'Mechanical keyboard с RGB', 'ELECTRONICS', 'NEW', 3500, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'orlov.ka@phystech.edu'), 'ACTIVE'),
                                                                                                ('Стул офисный', 'Офисный стул с регулировкой', 'HOME', 'NEW', 2500, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'orlov.ka@phystech.edu'), 'UNDER_MODERATION'),
                                                                                                ('Книги по программированию', 'Java, Python, Algorithms', 'BOOKS', 'USED', 1200, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'orlov.ka@phystech.edu'), 'DRAFT');

-- Валерия Новикова (novikova.vv@phystech.edu)
INSERT INTO ads (title, description, category, condition, price, location, user_id, status) VALUES
                                                                                                ('Микроскоп лабораторный', 'Профессиональный для исследований', 'OTHER', 'NEW', 15000, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'novikova.vv@phystech.edu'), 'ACTIVE'),
                                                                                                ('Набор реактивов', 'Для студенческих опытов', 'OTHER', 'BROKEN', 3000, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'novikova.vv@phystech.edu'), 'ACTIVE'),
                                                                                                ('Лабораторный халат', 'Белый халат размер M', 'CLOTHING', 'USED', 500, 'МФТИ, Долгопрудный', (SELECT id FROM users WHERE email = 'novikova.vv@phystech.edu'), 'UNDER_MODERATION');

SELECT '✅ Тестовые объявления созданы!' as result;