DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM service_groups_by_types) THEN
            INSERT INTO service_groups_by_types (id, type_id, service_group_id)
            VALUES (1, 3, 1),
                   (2, 3, 2),
                   (3, 3, 3);
        END IF;
    END;
$$;

-- Reset sequence
SELECT setval('service_groups_by_types_id_seq', (SELECT MAX(id) FROM service_groups_by_types));