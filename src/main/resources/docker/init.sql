CREATE SCHEMA auth;

CREATE TABLE auth.users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL
);
CREATE TABLE auth.roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE auth.users_roles
(
    user_id BIGINT REFERENCES auth.users(id),
    role_id SMALLINT REFERENCES auth.roles(id),
    PRIMARY KEY(user_id, role_id)
);

INSERT INTO auth.roles(name) VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('USER');
