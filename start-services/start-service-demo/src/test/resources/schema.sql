CREATE TABLE IF NOT EXISTS demo (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255),
    creator     VARCHAR(100),
    updater     VARCHAR(100),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted     BOOLEAN DEFAULT FALSE
);
