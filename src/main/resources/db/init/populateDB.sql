USE destinazioni;

-- 1. Insert two configurators
INSERT INTO users (nickname, password, role, first_login) VALUES
('admin1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'configurator', TRUE),
('admin2', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'configurator', TRUE);

-- 2. Insert five volunteers
INSERT INTO users (nickname, password, role, first_login) VALUES
('volunteer1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volunteer2', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volunteer3', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volunteer4', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volunteer5', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE);

-- 3. Insert ten final users
INSERT INTO users (nickname, password, role, first_login) VALUES
('user1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user2', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user3', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user4', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user5', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user6', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user7', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user8', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user9', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE),
('user10', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', TRUE);

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
INSERT INTO visit_types (title, description, meeting_point, start_date, end_date, start_time, duration, is_free, min_num_participants, max_num_participants, location_id) VALUES
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





INSERT INTO config (name, value) VALUES ('max_tickets_per_user_per_event', '10');
