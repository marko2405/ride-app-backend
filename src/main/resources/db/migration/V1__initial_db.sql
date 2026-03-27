CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(80) NOT NULL,
                       username VARCHAR(40) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'DRIVER', 'USER') NOT NULL DEFAULT 'USER',
                       enabled TINYINT(1) NOT NULL DEFAULT 1,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_username (username)
);