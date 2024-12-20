BEGIN;

-- Add a new admin
INSERT INTO admins (id, email, nickname, password, full_name, gender, phone_number, birth_date, address, social_url, updated_at, avatar_id)
VALUES (4, 'aifeeder@localhost', 'aifeeder', '$2a$12$TjXthC5BYhGKIr3BpT42eeK7OWCpfiPhZ9bkO/PFGsvfAp.u9/Ol6', 'AI Feeder', null, null, null, null, null, now(), null);

INSERT INTO groups (id, name) VALUES (3, 'AI_FEEDER');

INSERT INTO permissions (id, name) VALUES (10, 'ACCESS_LOGS');

INSERT INTO groups_permissions (group_id, permission_id) VALUES (3, 10);

INSERT INTO admins_groups (admin_id, group_id) VALUES (4, 3);

COMMIT;