BEGIN;

ALTER TABLE system_logs ADD COLUMN level VARCHAR(10) DEFAULT 'INFO' NOT NULL;

COMMIT;