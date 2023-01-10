DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users_roles;

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL
);
CREATE TABLE roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE users_roles
(
    user_id BIGINT REFERENCES users(id),
    role_id SMALLINT REFERENCES roles(id),
    PRIMARY KEY(user_id, role_id)
);

INSERT INTO users(username, email, password) VALUES
    ('Julio', 'julio@test.com', '$2a$10$e6H1Jgrft/scpmpzbMFO0uqF1gxqop73l5wOlwF30Aem6Tty1nI2G'),
    ('Cauan', 'cauan@test.com', '2a$10$SNsPkXTh0ryc82.D2HRJqOcY8sYh/TPnJW8WLqrERWkOq01ViWaCq'),
    ('Guest', 'guest@test.br', '$2a$10$SNsPkXTh0ryc82.D2HRJqOcY8sYh/TPnJW8WLqrERWkOq01ViWaCq');

INSERT INTO roles(name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_USER');

INSERT INTO users_roles(user_id, role_id) VALUES 
    (1, 2),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1);
