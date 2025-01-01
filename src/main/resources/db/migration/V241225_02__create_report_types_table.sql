CREATE TABLE report_types
(
    type INT NOT NULL CHECK (type IN (1, 2, 3)),
    category_id INT NOT NULL,

    CONSTRAINT pk__report_types PRIMARY KEY (type, category_id),
    CONSTRAINT fk__report_types__category_id FOREIGN KEY (category_id) REFERENCES report_categories (id)
);