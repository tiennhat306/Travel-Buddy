DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM report_types) THEN
            INSERT INTO report_types (type, category_id)
            VALUES (1, 1),
                        (1, 2),
                        (1, 3),
                        (1, 4),
                        (1, 5),
                        (2, 6),
                        (2, 7),
                        (2, 8),
                        (2, 9),
                        (2, 10),
                        (2, 11),
                        (3, 12),
                        (3, 13),
                        (3, 14),
                        (3, 15),
                        (1, 16),
                        (2, 16),
                        (3, 16);
        END IF;
    END;
$$;