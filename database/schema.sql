-- Reference schema. You do NOT need to run this by hand if you leave
-- spring.jpa.hibernate.ddl-auto=update in application.properties —
-- Hibernate will create these tables automatically on first run.
-- This file is here so you can see the exact structure, or run it
-- yourself if you prefer ddl-auto=validate/none.

CREATE DATABASE IF NOT EXISTS workout_db;
USE workout_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    muscle_group VARCHAR(50),
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS workouts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    workout_date DATE NOT NULL,
    notes VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS workout_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workout_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    sets INT NOT NULL,
    reps INT NOT NULL,
    weight DOUBLE,
    order_index INT DEFAULT 0,
    FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);

-- Seed a handful of common exercises so the app isn't empty on first run.
INSERT INTO exercises (name, muscle_group, description) VALUES
('Barbell Squat', 'Legs', 'Compound lower-body lift targeting quads, glutes, hamstrings'),
('Bench Press', 'Chest', 'Compound push targeting chest, shoulders, triceps'),
('Deadlift', 'Back', 'Full posterior-chain compound lift'),
('Pull-Up', 'Back', 'Bodyweight pull targeting lats and biceps'),
('Overhead Press', 'Shoulders', 'Standing barbell or dumbbell shoulder press'),
('Barbell Row', 'Back', 'Horizontal pull targeting mid-back and lats'),
('Bicep Curl', 'Arms', 'Isolation curl for biceps'),
('Tricep Pushdown', 'Arms', 'Cable isolation for triceps'),
('Plank', 'Core', 'Isometric core hold'),
('Running', 'Cardio', 'Steady-state or interval cardio');
