BEGIN;

-- Rename the table user_grading_policy to grading_ref
ALTER TABLE user_grading_policy RENAME TO grading_ref;

-- Rename the column pize_point to prize_point
ALTER TABLE grading_ref RENAME COLUMN pize_point TO prize_point;

COMMIT;