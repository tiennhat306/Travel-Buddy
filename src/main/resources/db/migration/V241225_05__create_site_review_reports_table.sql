CREATE TABLE site_review_reports
(
    report_id INT NOT NULL,
    site_review_id INT NOT NULL,

    CONSTRAINT pk__site_review_reports PRIMARY KEY (report_id),
    CONSTRAINT fk__site_review_reports__report_id FOREIGN KEY (report_id) REFERENCES reports (id),
    CONSTRAINT fk__site_review_reports__site_review_id FOREIGN KEY (site_review_id) REFERENCES site_reviews (id)
);