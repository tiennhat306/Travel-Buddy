CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,                -- ID chính của bảng
    user_id INT NOT NULL,                 -- Người nhận thông báo, khóa ngoại
    type INT NOT NULL,               -- Loại thông báo (like, review, plan_change, etc.)
    entity_type INT,
    entity_id INT,               -- ID của bài viết, review, hoặc kế hoạch du lịch
    content JSONB,                  -- Nội dung tổng hợp thông báo
    --    count INT DEFAULT 1,                     -- Số lượng sự kiện đã gom nhóm
    is_read BOOLEAN DEFAULT FALSE,           -- Đã đọc hay chưa
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời gian cập nhật lần cuối

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);