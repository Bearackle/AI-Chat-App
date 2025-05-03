use `aichat`;

CREATE TABLE ums_user (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `username` VARCHAR(50) NOT NULL UNIQUE,
                          `email` VARCHAR(100) NOT NULL UNIQUE,
                          `status` int(1),
                          `password` VARCHAR(255) NOT NULL,
                          `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- Bảng ChatSessions: Lưu thông tin phiên trò chuyện
CREATE TABLE cms_chat_session (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `user_id` BIGINT NOT NULL,
                                  `title` VARCHAR(255) NULL,
                                  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`user_id`) REFERENCES ums_user(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng Messages: Lưu tin nhắn trong phiên trò chuyện
CREATE TABLE cms_message (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `session_id` BIGINT NOT NULL,
                             `user_id` BIGINT NOT NULL,
                             `content` TEXT NOT NULL,
                             `is_user` INT(1) NOT NULL, -- 1 nếu là tin nhắn từ người dùng, 0 nếu từ AI
                             `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             FOREIGN KEY (`session_id`) REFERENCES cms_chat_session(`id`) ON DELETE CASCADE,
                             FOREIGN KEY (`user_id`) REFERENCES ums_user(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tạo chỉ mục (index) để tối ưu hiệu suất
CREATE INDEX idx_session_user ON cms_chat_session(`id`);
CREATE INDEX idx_message_session ON cms_message(`session_id`);
CREATE INDEX idx_message_user ON cms_message(`user_id`);

use `aichat`;
select * from cms_message;
