DELETE FROM ads;
DELETE FROM users;
DELETE FROM moderators;


INSERT INTO users (email, name, password, address, study_program, course, rating, coins)
VALUES ('shabunina.ao@phystech.edu', 'Анастасия', 'pass_strong123', 'Долгопрудный', 'ВШПИ', 1, 4.95, 100),
       ('ivanov.ii@phystech.edu', 'Мария Соколова', 'physics2024', 'Москва, Ленинский проспект', 'Общая физика', 3,
        4.8, 0),
       ('orlov.ka@phystech.edu', 'Дмитрий Орлов', 'qwerty123', 'Долгопрудный, ул. Первомайская', 'Информатика', 1, 3.9,
        78),
       ('novikova.vv@phystech.edu', 'Варерия Новикова', 'anna_pass', 'Москва, ул. Вавилова', 'Биофизика', 4, 4.7, 15),
       ('doronina.ea@phystech.edu', 'Елизавета', 'StrongPass123', 'Долгопрудный, ул. Первомайская', 'Программная инженерия', 1, 3.9,
        78),
       ('orlova.ek@phystech.edu', 'Елизавета', 'lisa_orel123', 'Москва, ул. Вавилова', 'Программная инжерени', 1, 4.7, 15000);


INSERT INTO moderators (email, name, password, permissions)
VALUES ('lvov.ap@phystech.edu', 'moderator_1', 'p8ss123', '{}'),
       ('ivanov.ii@phystech.edu', 'moder_2', 'physics2024', '{}'),
       ('orlov.ka@phystech.edu', 'mod_3', 'qwerty103', '{}'),
       ('novikova.vv@phystech.edu', 'moderator4', 'anna_pass', '{}');

