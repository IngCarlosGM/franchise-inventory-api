CREATE TABLE IF NOT EXISTS franchise (
    id            VARCHAR(36)  PRIMARY KEY,
    name          VARCHAR(120) NOT NULL UNIQUE,
    contact_email VARCHAR(120),
    website       VARCHAR(200),
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL
);