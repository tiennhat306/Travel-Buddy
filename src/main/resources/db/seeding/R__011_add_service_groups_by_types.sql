DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM service_groups_by_types) THEN
            INSERT INTO service_groups_by_types (id, type_id, service_group_id)
            VALUES (1, 3, 1),
                   (2, 3, 2),
                   (3, 3, 3),
                   (4, 5, 4),
                   (5, 6, 4),
                   (6, 5, 4),
                   (7, 6, 4),
                   (8, 7, 4),
                   (9, 8, 4),
                   (10, 9, 4),
                   (11, 10, 4),
                   (12, 11, 4),
                   (13, 12, 4),
                   (14, 13, 4),
                   (15, 14, 4),
                   (16, 15, 4),
                   (17, 16, 4),
                   (18, 17, 4),
                   (19, 18, 4),
                   (20, 19, 4),
                   (21, 20, 4),
                   (22, 53, 4),
                   (23, 2, 5),
                   (24, 3, 5),
                   (25, 4, 5),
                   (26, 5, 5),
                   (27, 6, 5),
                   (28, 7, 5),
                   (29, 8, 5),
                   (30, 9, 5),
                   (31, 10, 5),
                   (32, 11, 5),
                   (33, 12, 5),
                   (34, 13, 5),
                   (35, 14, 5),
                   (36, 15, 5),
                   (37, 16, 5),
                   (38, 17, 5),
                   (39, 18, 5),
                   (40, 19, 5),
                   (41, 20, 5),
                   (42, 53, 5),
                   (43, 27, 1),
                   (44, 27, 2),
                   (159, 27, 3),
                   (45, 40, 1),
                   (46, 40, 2),
                   (47, 40, 3),
                   (48, 34, 1),
                   (49, 34, 2),
                   (50, 34, 3),

                   (51, 1, 7),
                   (52, 2, 7),
                   (53, 3, 7),
                   (54, 4, 7),
                   (55, 5, 7),
                   (56, 6, 7),
                   (57, 7, 7),
                   (58, 8, 7),
                   (59, 9, 7),
                   (60, 10, 7),
                   (61, 11, 7),
                   (62, 12, 7),
                   (63, 13, 7),
                   (64, 14, 7),
                   (65, 15, 7),
                   (66, 16, 7),
                   (67, 17, 7),
                   (68, 18, 7),
                   (69, 19, 7),
                   (70, 20, 7),
                   (71, 21, 7),
                   (72, 22, 7),
                   (73, 23, 7),
                   (74, 24, 7),
                   (75, 25, 7),
                   (76, 26, 7),
                   (77, 27, 7),
                   (78, 28, 7),
                   (79, 29, 7),
                   (80, 30, 7),
                   (81, 31, 7),
                   (82, 32, 7),
                   (83, 33, 7),
                   (84, 34, 7),
                   (85, 35, 7),
                   (86, 36, 7),
                   (87, 37, 7),
                   (88, 38, 7),
                   (89, 39, 7),
                   (90, 40, 7),
                   (91, 41, 7),
                   (92, 42, 7),
                   (93, 43, 7),
                   (94, 44, 7),
                   (95, 45, 7),
                   (96, 46, 7),
                   (97, 47, 7),
                   (98, 48, 7),
                   (99, 49, 7),
                   (100, 50, 7),
                   (101, 51, 7),
                   (102, 52, 7),
                   (103, 53, 7),

                   (104, 1, 6),
                   (105, 38, 6),

                   (126, 21, 5),
                   (127, 22, 5),
                   (128, 23, 5),
                   (129, 24, 5),
                   (130, 25, 5),
                   (131, 26, 5),
                   (132, 27, 5),
                   (133, 28, 5),
                   (134, 29, 5),
                   (135, 30, 5),
                   (136, 31, 5),
                   (137, 32, 5),
                   (138, 33, 5),
                   (139, 34, 5),
                   (140, 35, 5),
                   (141, 36, 5),
                   (142, 37, 5),
                   (143, 38, 5),
                   (144, 39, 5),
                   (145, 40, 5),
                   (146, 41, 5),
                   (147, 42, 5),
                   (148, 43, 5),
                   (149, 44, 5),
                   (150, 45, 5),
                   (151, 46, 5),
                   (152, 47, 5),
                   (153, 48, 5),
                   (154, 49, 5),
                   (155, 50, 5),
                   (156, 51, 5),
                   (157, 52, 5);
        END IF;
    END;
$$;

-- Reset sequence
SELECT setval('service_groups_by_types_id_seq', (SELECT MAX(id) FROM service_groups_by_types));