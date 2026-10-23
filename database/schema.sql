-- =========================================================
-- NIET Event Management Portal - Database Schema
-- =========================================================

CREATE DATABASE IF NOT EXISTS niet_event_portal;
USE niet_event_portal;

-- ---------------------------------------------------------
-- ADMIN TABLE
-- Only registered admins (rows in this table) can log in
-- to the Admin Dashboard.
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- store a hashed password in production (e.g. BCrypt)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default admin login -> username: admin | password: admin123
-- (Change this immediately after first login in a real deployment)
INSERT INTO admin (username, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE username = username;

-- ---------------------------------------------------------
-- EVENTS TABLE (master list of all events)
-- table_name column stores the name of the DEDICATED
-- registration table created for that specific event
-- (see EventDAO.addEvent in the Java backend). Every event
-- therefore gets its own registration table, with the
-- columns matching exactly what the student fills in the
-- registration form: name, email, phone, department, year.
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    venue VARCHAR(150),
    image_url VARCHAR(500),
    table_name VARCHAR(100) NOT NULL UNIQUE, -- e.g. reg_annual_day_2026
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Sample events (for first run / testing).
-- The Java backend automatically creates a matching
-- registration table (reg_<event_name>) for each of these
-- the first time EventDAO bootstraps, OR you can create
-- them manually here for a quick start:
-- ---------------------------------------------------------
INSERT INTO events (name, description, event_date, venue, image_url, table_name) VALUES
('Annual Day 2026', 'A celebration of student achievements with cultural performances and prize distribution.', '2026-09-15', 'Main Auditorium', 'https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=600', 'reg_annual_day_2026'),
('Tech Fest', 'Coding contests, hackathons, robotics and project expos across all departments.', '2026-09-28', 'Engineering Block', 'https://images.unsplash.com/photo-1531482615713-2afd69097998?w=600', 'reg_tech_fest'),
('Pongal Celebration', 'Traditional harvest festival celebrated with games, food stalls and cultural events.', '2026-10-05', 'Open Ground', 'https://images.unsplash.com/photo-1604608672516-f1b9b1d37076?w=600', 'reg_pongal_celebration'),
('Annual Sports Meet', 'Inter-department athletics, cricket, football and indoor sports competitions.', '2026-10-20', 'Sports Complex', 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600', 'reg_annual_sports_meet');

-- ---------------------------------------------------------
-- EXAMPLE of a per-event registration table (this exact
-- structure is what EventDAO.java generates dynamically
-- whenever the admin adds a new event from the dashboard).
-- Column names are exactly the details filled by the
-- student in the registration form.
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS reg_annual_day_2026 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_email_per_event (email)  -- prevents duplicate registration by same email
);

CREATE TABLE IF NOT EXISTS reg_tech_fest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_email_per_event (email)
);

CREATE TABLE IF NOT EXISTS reg_pongal_celebration (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_email_per_event (email)
);

CREATE TABLE IF NOT EXISTS reg_annual_sports_meet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_email_per_event (email)
);

-- Note: when the admin ADDS a new event through the dashboard,
-- EventDAO.java automatically executes the equivalent of the
-- CREATE TABLE statements above with a sanitized table name.
-- When the admin DELETES an event, EventDAO.java runs
-- DROP TABLE on that event's registration table and removes
-- its row from the events table.
