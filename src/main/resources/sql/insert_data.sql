DELETE FROM ads;
DELETE FROM users;

-- Вставляем пользователей (id будет автоматически сгенерирован)
INSERT INTO users (email, name, password, address, study_program, course, rating, coins) VALUES
                                                                                             ('shabunina.ao@phystech.edu', 'Анастасия Шабунина', 'pass123', 'Долгопрудный', 'ВШПИ', 1, 4.95, 100),
                                                                                             ('physics_lover@phystech.edu', 'Мария Соколова', 'physics2024', 'Москва, Ленинский проспект', 'Общая физика', 3, 4.8, 0),
                                                                                             ('coder42@phystech.edu', 'Дмитрий Орлов', 'qwerty123', 'Долгопрудный, ул. Первомайская', 'Информатика', 1, 3.9, 78),
                                                                                             ('science_queen@phystech.edu', 'Анна Новикова', 'anna_pass', 'Москва, ул. Вавилова', 'Биофизика', 4, 4.7, 15);

