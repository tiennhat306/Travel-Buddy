CREATE TABLE user_reports
(
    report_id INT NOT NULL,
    user_id INT NOT NULL,

    CONSTRAINT pk__user_reports PRIMARY KEY (report_id),
    CONSTRAINT fk__user_reports__report_id FOREIGN KEY (report_id) REFERENCES reports (id),
    CONSTRAINT fk__user_reports__user_id FOREIGN KEY (user_id) REFERENCES users (id)
);