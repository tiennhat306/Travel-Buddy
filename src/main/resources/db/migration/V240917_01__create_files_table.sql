START TRANSACTION ;

CREATE TABLE files(
    id VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk__files PRIMARY KEY (id)
);
CREATE INDEX idx__files__created_at ON files USING BTREE (created_at);

COMMIT ;