CREATE TABLE site_reports
(
    report_id INT NOT NULL,
    site_id INT NOT NULL,

    CONSTRAINT pk__site_reports PRIMARY KEY (report_id),
    CONSTRAINT fk__site_reports__report_id FOREIGN KEY (report_id) REFERENCES reports (id),
    CONSTRAINT fk__site_reports__site_id FOREIGN KEY (site_id) REFERENCES sites (id)
);