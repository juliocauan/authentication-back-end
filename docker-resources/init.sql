CREATE SCHEMA IF NOT EXISTS auth;

DROP TABLE IF EXISTS auth.users;
DROP TABLE IF EXISTS auth.roles;
DROP TABLE IF EXISTS auth.users_roles;

CREATE TABLE auth.users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    name VARCHAR(128) NOT NULL,
    email VARCHAR(128) UNIQUE NOT NULL,
    secret VARCHAR(255) NOT NULL);
CREATE TABLE auth.roles(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(15) NOT NULL);
CREATE TABLE auth.users_roles(
    user_id UUID REFERENCES auth.users(id),
    role_id SMALLINT REFERENCES auth.roles(id),
    PRIMARY KEY(user_id, role_id));

INSERT INTO auth.users(name, email, secret) VALUES
    ('Julio', 'julio@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');
INSERT INTO auth.users(name, email, secret) VALUES
    ('Cauan', 'cauan@test.com', '$2a$10$ofEy..aODV5QleKty0kkJ.8UXdOXIdr/CeyXswcjJGBVYgxU296NK');

INSERT INTO auth.roles(name) VALUES ('ADMIN');
INSERT INTO auth.roles(name) VALUES ('CLIENT');
