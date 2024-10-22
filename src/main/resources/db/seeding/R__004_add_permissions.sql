DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM permissions) THEN
            INSERT INTO permissions  (id, name)
            VALUES (1, 'MANAGE_USERS'),
                   (2, 'MANAGE_GROUPS'),
                   (3, 'MANAGE_PERMISSIONS'),
                   (4, 'MANAGE_ROLES'),
                   (5, 'MANAGE_CATEGORIES'),
                   (6, 'MANAGE_POSTS'),
                   (7, 'MANAGE_COMMENTS'),
                   (8, 'MANAGE_TAGS');
        END IF;
    END;
$$;