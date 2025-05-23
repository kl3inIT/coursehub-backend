-- AWS Test Schema - Basic tables for testing
-- Drop tables if they exist (for testing purposes)
DROP TABLE IF EXISTS `lessons`;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `users`;

-- Create users table
CREATE TABLE `users`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `avatar` VARCHAR(255) NULL,
    `created_at` DATETIME NULL,
    `updated_at` DATETIME NULL,
    `is_active` BIGINT NULL DEFAULT '1'
);

-- Add unique constraint for email
ALTER TABLE `users` ADD UNIQUE `users_email_unique`(`email`);

-- Create courses table
CREATE TABLE `courses`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(255) NULL,
    `price` DECIMAL(8, 2) NULL,
    `discount` DECIMAL(8, 2) NULL,
    `description` TEXT NULL,
    `thumbnail` TEXT NULL,
    `created_at` DATETIME NULL,
    `updated_at` DATETIME NULL,
    `is_active` BIGINT NULL DEFAULT '1'
);

-- Create lessons table
CREATE TABLE `lessons`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `course_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(255) NULL,
    `video_url` TEXT NULL,
    `description` TEXT NULL,
    `order_number` BIGINT NULL,
    `created_at` DATETIME NULL,
    `updated_at` DATETIME NULL,
    `is_active` BIGINT NULL DEFAULT '1'
);

-- Add foreign key constraints
ALTER TABLE `courses` ADD CONSTRAINT `courses_user_id_foreign` FOREIGN KEY(`user_id`) REFERENCES `users`(`id`);
ALTER TABLE `lessons` ADD CONSTRAINT `lessons_course_id_foreign` FOREIGN KEY(`course_id`) REFERENCES `courses`(`id`);

-- Insert sample data for testing
INSERT INTO `users` (`name`, `email`, `password`, `avatar`, `created_at`, `updated_at`, `is_active`) VALUES
('John Doe', 'john@example.com', 'password123', 'avatar1.jpg', NOW(), NOW(), 1),
('Jane Smith', 'jane@example.com', 'password456', 'avatar2.jpg', NOW(), NOW(), 1),
('Bob Johnson', 'bob@example.com', 'password789', 'avatar3.jpg', NOW(), NOW(), 1);

INSERT INTO `courses` (`user_id`, `title`, `price`, `discount`, `description`, `thumbnail`, `created_at`, `updated_at`, `is_active`) VALUES
(1, 'Introduction to Java', 99.99, 19.99, 'Learn Java programming from scratch', 'java-course.jpg', NOW(), NOW(), 1),
(1, 'Spring Boot Mastery', 149.99, 29.99, 'Master Spring Boot framework', 'spring-course.jpg', NOW(), NOW(), 1),
(2, 'React for Beginners', 79.99, 15.99, 'Build modern web apps with React', 'react-course.jpg', NOW(), NOW(), 1),
(2, 'Node.js Backend Development', 119.99, 23.99, 'Server-side development with Node.js', 'nodejs-course.jpg', NOW(), NOW(), 1),
(3, 'AWS Cloud Computing', 199.99, 39.99, 'Learn AWS services and deployment', 'aws-course.jpg', NOW(), NOW(), 1);

INSERT INTO `lessons` (`course_id`, `title`, `video_url`, `description`, `order_number`, `created_at`, `updated_at`, `is_active`) VALUES
-- Java Course Lessons
(1, 'Java Basics and Syntax', 'https://youtube.com/watch?v=java1', 'Introduction to Java programming language', 1, NOW(), NOW(), 1),
(1, 'Object-Oriented Programming', 'https://youtube.com/watch?v=java2', 'Learn OOP concepts in Java', 2, NOW(), NOW(), 1),
(1, 'Collections and Data Structures', 'https://youtube.com/watch?v=java3', 'Working with Java collections', 3, NOW(), NOW(), 1),

-- Spring Boot Course Lessons
(2, 'Spring Boot Introduction', 'https://youtube.com/watch?v=spring1', 'Getting started with Spring Boot', 1, NOW(), NOW(), 1),
(2, 'REST APIs with Spring Boot', 'https://youtube.com/watch?v=spring2', 'Building RESTful web services', 2, NOW(), NOW(), 1),
(2, 'Database Integration', 'https://youtube.com/watch?v=spring3', 'Connect Spring Boot with databases', 3, NOW(), NOW(), 1),

-- React Course Lessons
(3, 'React Fundamentals', 'https://youtube.com/watch?v=react1', 'Learn React basics and components', 1, NOW(), NOW(), 1),
(3, 'State Management', 'https://youtube.com/watch?v=react2', 'Managing state in React applications', 2, NOW(), NOW(), 1),
(3, 'React Hooks', 'https://youtube.com/watch?v=react3', 'Modern React with hooks', 3, NOW(), NOW(), 1),

-- Node.js Course Lessons
(4, 'Node.js Basics', 'https://youtube.com/watch?v=node1', 'Introduction to Node.js runtime', 1, NOW(), NOW(), 1),
(4, 'Express.js Framework', 'https://youtube.com/watch?v=node2', 'Building web servers with Express', 2, NOW(), NOW(), 1),
(4, 'Database Operations', 'https://youtube.com/watch?v=node3', 'Working with databases in Node.js', 3, NOW(), NOW(), 1),

-- AWS Course Lessons
(5, 'AWS Overview', 'https://youtube.com/watch?v=aws1', 'Introduction to Amazon Web Services', 1, NOW(), NOW(), 1),
(5, 'EC2 and S3 Services', 'https://youtube.com/watch?v=aws2', 'Working with EC2 instances and S3 storage', 2, NOW(), NOW(), 1),
(5, 'Deployment Strategies', 'https://youtube.com/watch?v=aws3', 'Deploying applications on AWS', 3, NOW(), NOW(), 1); 