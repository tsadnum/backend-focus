INSERT INTO roles (name) VALUES ( 'ROLE_USER');
INSERT INTO roles ( name) VALUES ( 'ROLE_ADMIN');

INSERT INTO users (email,password,created_at,status,first_name,last_name,last_login_at) VALUES ('test@focus.com','$2a$10$wwL.cJNXI70MVnlhkrr5VuThV64oEvlfW22xBPx2MUL5lQf/nYchy',NOW(),'ACTIVE','Test','User',NULL);

INSERT INTO user_roles (user_id, role_id)VALUES(1, 2);

INSERT INTO tasks (user_id, title, description, type, status, priority,start_date, due_date, kanban_order, created_at, updated_at)VALUES( 1, 'Estudiar Spring Boot', 'Revisar seguridad y JWT', 'STUDY', 'IN_PROGRESS', 'HIGH',CURRENT_DATE, CURRENT_DATE + 2, 1, NOW(), NOW());

INSERT INTO goals ( user_id, title, description, progress, target_date, created_at, updated_at)VALUES( 1, 'Completar módulo backend', 'Finalizar endpoints y seguridad', 50.0, CURRENT_DATE + 10, NOW(), NOW());

INSERT INTO events ( user_id, title, description, start_date_time, end_date_time,location, created_at, updated_at)VALUES( 1, 'Reunión con mentor', 'Hablar sobre arquitectura de microservicios',NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day 1 hour','Google Meet', NOW(), NOW());

INSERT INTO diary_entries ( user_id, title, content, entry_date, created_at, updated_at) VALUES (1, 'Primer día con Focus', 'Hoy comencé a usar Focus para organizarme mejor.', CURRENT_DATE, NOW(), NOW());
