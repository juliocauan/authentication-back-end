DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users_roles;

CREATE TABLE users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    name VARCHAR(128) NOT NULL,
    email VARCHAR(128) UNIQUE NOT NULL,
    secret VARCHAR(255) NOT NULL
);
CREATE TABLE roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE users_roles
(
    user_id UUID REFERENCES users(id),
    role_id SMALLINT REFERENCES roles(id),
    PRIMARY KEY(user_id, role_id)
);

INSERT INTO users(name, email, secret) VALUES
    ('Julio', 'julio@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');
INSERT INTO users(name, email, secret) VALUES
    ('Cauan', 'cauan@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');

INSERT INTO roles(name) VALUES ('ADMIN');
INSERT INTO roles(name) VALUES ('CLIENT');
