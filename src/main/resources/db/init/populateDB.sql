USE destinazioni;

-- 1. Insert two configurators
INSERT INTO users (nickname, password, role, firstLogin) VALUES
('admin1', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'configurator', FALSE),
('admin2', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'configurator', FALSE);

-- 2. Insert five volunteers
INSERT INTO users (nickname, password, role, firstLogin) VALUES
('volunteer1', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'volunteer', FALSE),
('volunteer2', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'volunteer', FALSE),
('volunteer3', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'volunteer', FALSE),
('volunteer4', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'volunteer', FALSE),
('volunteer5', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'volunteer', FALSE);

-- 3. Insert ten final users
INSERT INTO users (nickname, password, role, firstLogin) VALUES
('user1', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user2', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user3', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user4', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user5', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user6', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user7', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user8', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user9', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE),
('user10', '$2a$10$xD7J5v6JZz5U5Z5U5Z5U5e', 'finalUser', FALSE);

-- 4. Insert two towns in area_of_interest
INSERT INTO area_of_interest (town) VALUES
('Milano'),
('Monza');

-- 5. Insert three tourist locations (2 in Milano, 1 in Monza)
INSERT INTO locations (name, description) VALUES
('Duomo di Milano', 'The magnificent Gothic cathedral in the heart of Milan'),
('Castello Sforzesco', 'Historic castle with museums and art collections'),
('Villa Reale di Monza', 'Neoclassical royal villa with beautiful gardens');

-- Insert addresses for these locations
INSERT INTO location_addresses (street, number, town, province, location_id) VALUES
('Piazza del Duomo', '1', 'Milano', 'MI', 1),
('Piazza Castello', '1', 'Milano', 'MI', 2),
('Viale Brianza', '1', 'Monza', 'MB', 3);

-- 6. Insert five visit types (with different start times)
INSERT INTO visit_types (title, description, meetingPoint, startDate, endDate, startTime, duration, isFree, minNumParticp, maxNumPartec, location_id) VALUES
('Duomo Guided Tour', 'Complete tour of the cathedral and terraces', 'Main entrance', '2024-03-01', '2024-12-31', '09:00:00', 120, FALSE, 5, 20, 1),
('Duomo Roof Access', 'Access to the cathedral terraces with panoramic views', 'Ticket office', '2024-03-01', '2024-12-31', '11:00:00', 90, FALSE, 2, 15, 1),
('Castle Museums Tour', 'Guided tour of the castle museums', 'Courtyard fountain', '2024-04-01', '2024-11-30', '10:30:00', 90, TRUE, 3, 15, 2),
('Villa Reale Gardens', 'Guided walk through the royal gardens', 'Main gate', '2024-05-01', '2024-10-31', '14:00:00', 60, TRUE, 2, 10, 3),
('Castle Secret Passages', 'Exclusive tour of hidden castle passages', 'East gate', '2024-06-01', '2024-09-30', '15:30:00', 120, FALSE, 4, 12, 2);

-- 7. Insert visit days for each visit type
INSERT INTO visit_days (visit_type_id, day_of_week) VALUES
(1, 'Monday'), (1, 'Wednesday'), (1, 'Friday'),
(2, 'Tuesday'), (2, 'Thursday'), (2, 'Saturday'),
(3, 'Wednesday'), (3, 'Sunday'),
(4, 'Friday'), (4, 'Saturday'), (4, 'Sunday'),
(5, 'Saturday');

-- 8. Insert actual visits for each visit type
INSERT INTO visits (date, volunteer_nickname, status, visit_type_id) VALUES
('2024-03-04', 'volunteer1', 'completed', 1),  -- Monday
('2024-03-05', 'volunteer2', 'completed', 2),  -- Tuesday
('2024-04-03', 'volunteer3', 'completed', 3),  -- Wednesday
('2024-05-03', 'volunteer4', 'completed', 4),  -- Friday
('2024-06-01', 'volunteer5', 'completed', 5);  -- Saturday

-- Insert visitors for each visit
INSERT INTO visitors (visit_id, visitor_nickname) VALUES
(1, 'user1'), (1, 'user2'), (1, 'user3'), (1, 'user4'), (1, 'user5'),
(2, 'user6'), (2, 'user7'), (2, 'user8'),
(3, 'user9'), (3, 'user10'), (3, 'user1'), (3, 'user2'),
(4, 'user3'), (4, 'user4'), (4, 'user5'),
(5, 'user6'), (5, 'user7'), (5, 'user8'), (5, 'user9');