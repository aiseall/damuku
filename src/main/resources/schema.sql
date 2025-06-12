-- 创建弹幕表
CREATE TABLE IF NOT EXISTS danmaku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    color VARCHAR(7) DEFAULT '#FFFFFF',
    font_size INT DEFAULT 24,
    time DOUBLE NOT NULL,
    video_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64),
    username VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引优化（按视频ID和时间查询）
CREATE INDEX idx_video_time ON danmaku(video_id, time);
    