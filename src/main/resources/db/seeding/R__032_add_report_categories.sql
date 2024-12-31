DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM report_categories) THEN
            INSERT INTO report_categories (id, name, description, enabled)
            VALUES (1, 'Lừa đảo, gian lận hoặc mạo danh', 'Description 1', TRUE),
                        (2, 'Nội dung người lớn', 'Description 2', TRUE),
                        (3, 'Bán hoặc quảng cáo mặt hàng bị hạn chế', 'Description 3', TRUE),
                        (4, 'Thông tin sai sự thật', 'Description 4', TRUE),
                        (5, 'Nội dung mang tính bạo lực, thù ghét hoặc gây phiền toái', 'Description 5', TRUE),
                        (6, 'Đánh giá mang tính xúc phạm, khiêu dâm hoặc chứa lời nói thể hiện sự căm ghét', 'Description 6', TRUE),
                        (7, 'Nội dung bất hợp pháp', 'Description 7', TRUE),
                        (8, 'Đánh giá mang tính thiên vị hoặc được viết bởi người có liên kết với cơ sở kinh doanh', 'Description 8', TRUE),
                        (9, 'Doanh nghiệp này đã đóng cửa hoặc không tồn tại', 'Description 9', TRUE),
                        (10, 'Đánh giá không mô tả trải nghiệm cá nhân', 'Description 10', TRUE),
                        (11, 'Đánh giá chứa thông tin sai lệch hoặc gây hiểu nhầm', 'Description 11', TRUE),
                        (12, 'Tài khoản giả mạo', 'Description 12', TRUE),
                        (13, 'Tên không phù hợp', 'Description 13', TRUE),
                        (14, 'Ảnh không phù hợp', 'Description 14', TRUE),
                        (15, 'Quấy rối hoặc bắt nạt', 'Description 15', TRUE),
                        (16, 'Vấn đề khác', 'Description 16', TRUE);
        END IF;
    END;
$$;

-- Reset sequence
SELECT setval('report_categories_id_seq', (SELECT MAX(id) FROM report_categories));