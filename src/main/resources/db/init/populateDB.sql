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

INSERT INTO config (name, value) VALUES ('max_tickets_per_user_per_event', '10');
