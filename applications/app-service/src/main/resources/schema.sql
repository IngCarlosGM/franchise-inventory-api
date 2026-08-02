CREATE TABLE IF NOT EXISTS franchise (
    id            VARCHAR(36)  PRIMARY KEY,
    name          VARCHAR(120) NOT NULL UNIQUE,
    contact_email VARCHAR(120),
    website       VARCHAR(200),
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL
);

CREATE TABLE IF NOT EXISTS branch (
    id           VARCHAR(36)  PRIMARY KEY,
    franchise_id VARCHAR(36)  NOT NULL REFERENCES franchise(id) ON DELETE CASCADE,
    name         VARCHAR(120) NOT NULL,
    city         VARCHAR(80),
    phone        VARCHAR(20),
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    UNIQUE (franchise_id, name)
);