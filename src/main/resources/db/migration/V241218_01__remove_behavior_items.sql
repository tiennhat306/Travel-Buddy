BEGIN;

-- Drop column behavior_id from user_behavior_logs with its FK
ALTER TABLE user_behavior_logs DROP COLUMN IF EXISTS behavior_id;

-- Remove the behavior_items table
DROP TABLE IF EXISTS user_behaviors;

-- Add a column of behavior to user_behavior_logs of type String
ALTER TABLE user_behavior_logs ADD COLUMN behavior VARCHAR(100);

ALTER TABLE user_behavior_logs
RENAME COLUMN site_version_id TO site_id;

COMMIT;