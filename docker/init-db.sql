-- 初始化数据库表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    full_name VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 插入示例数据
INSERT INTO users (username, password, email, phone, full_name, created_at, updated_at)
VALUES ('admin', '$2a$10$eVw7kD5u93qN2cQ1Q5Q6ROQ6Nv7zq3U3q3U3q3U3q3U3q3U3q3U3', 'admin@example.com', '1234567890', 'Administrator', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();