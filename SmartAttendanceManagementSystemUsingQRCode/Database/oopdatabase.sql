CREATE DATABASE SmartAttendance;
USE SmartAttendance;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'Student' -- Admin or Student
);

CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    status VARCHAR(20) DEFAULT 'Present',
    scan_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password, full_name, role) 
VALUES ('admin', 'admin123', 'System Admin', 'Admin');

select * from USERS;
drop database smartattendance;
