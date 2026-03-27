ALTER TABLE users
ADD COLUMN first_name VARCHAR(100) NOT NULL AFTER id,
ADD COLUMN last_name VARCHAR(100) NOT NULL AFTER first_name;

CREATE TABLE driver_profiles (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL UNIQUE,
                                 license_number VARCHAR(100),
                                 years_of_experience INT,
                                 active BIT NOT NULL DEFAULT 1,
                                 average_rating DOUBLE NOT NULL DEFAULT 0,
                                 total_ratings INT NOT NULL DEFAULT 0,
                                 CONSTRAINT fk_driver_profiles_user
                                     FOREIGN KEY (user_id) REFERENCES users(id)
                                         ON DELETE CASCADE
);