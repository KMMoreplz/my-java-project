CREATE TABLE IF NOT EXISTS album
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(256) NOT NULL,
    release_year INTEGER      NOT NULL
);
