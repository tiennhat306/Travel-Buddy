BEGIN;

INSERT INTO permissions (id, name) VALUES (11, 'MANAGE_REPORTS');

INSERT INTO groups_permissions (group_id, permission_id) VALUES (1, 11);

COMMIT;