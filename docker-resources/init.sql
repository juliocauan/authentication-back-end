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
    ('Julio', 'julio@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');
INSERT INTO users(username, email, password) VALUES
    ('Cauan', 'cauan@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');

INSERT INTO roles(name) VALUES ('ROLE_ADMIN');
INSERT INTO roles(name) VALUES ('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES ('ROLE_USER');

INSERT INTO users_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO users_roles(user_id, role_id) VALUES (2, 3);
