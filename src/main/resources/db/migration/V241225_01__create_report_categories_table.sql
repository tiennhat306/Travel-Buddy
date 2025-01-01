CREATE TABLE report_categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,

    CONSTRAINT unique_name UNIQUE (name)
);