CREATE TABLE reports
(
    id              SERIAL PRIMARY KEY,
    description     VARCHAR(255),
    reported_by     INTEGER,
    created_at      TIMESTAMP,
    category_id INTEGER,

    CONSTRAINT fk_report_category FOREIGN KEY (category_id) REFERENCES report_categories (id),
    CONSTRAINT fk_report_user FOREIGN KEY (reported_by) REFERENCES users (id)
);