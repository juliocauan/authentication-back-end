CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.users
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(60) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS auth.roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(40) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS auth.users_roles
(
    user_id INTEGER REFERENCES auth.users(id),
    role_id SMALLINT REFERENCES auth.roles(id),
    PRIMARY KEY(user_id, role_id)
);
CREATE TABLE IF NOT EXISTS auth.password_reset_tokens
(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES auth.users(id) ON DELETE CASCADE UNIQUE NOT NULL,
    token VARCHAR(43) UNIQUE NOT NULL,
    expire_date timestamp NOT NULL
);
