CREATE DATABASE IF NOT EXISTS campus_events
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_events;

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_student_event UNIQUE (student_id, event_name)
);

-- Optional sample rows for testing the filter and table.
INSERT IGNORE INTO registrations (student_id, student_name, event_name)
VALUES
    ('STU001', 'Sample Student One', 'Tech Symposium'),
    ('STU002', 'Sample Student Two', 'Cultural Fest'),
    ('STU003', 'Sample Student Three', 'Hackathon');
