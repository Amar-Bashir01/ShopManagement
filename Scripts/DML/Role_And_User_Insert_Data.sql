INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'USER');

-- User: amar / amar
INSERT INTO users (username, email, password_hash, role_id, created_at, updated_at)
VALUES ('amar', 'amar@example.com',
        '$2a$10$hDdpV/.COCMuiD1XakE13OvAcmDBB1Znsl8gGtjDixqrgcg0CAVn2', -- password = amar
        1, NOW(), NOW());

-- User: bhanu / bhanu
INSERT INTO users (username, email, password_hash, role_id, created_at, updated_at)
VALUES ('bhanu', 'bhanu@example.com',
        '$2a$10$yB9n0YwDYo7C0Zq5L0L0Jub9iT5m0TSxGrvZKxJ7vtknXo0nU7vqa', -- password = bhanu
        2, NOW(), NOW());
