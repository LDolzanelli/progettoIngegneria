USE destinazioni;

-- Inserimento degli amministratori
INSERT INTO users (nickname, password, role, first_login) VALUES
('admin1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'configurator', TRUE),
('admin2', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'configurator', TRUE);

-- Inserimento dei volontari
INSERT INTO users (nickname, password, role, first_login) VALUES
('volontario_marco', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volontario_giovanni', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE),
('volontario_luca', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'volunteer', TRUE);

-- Inserimento fruitori
INSERT INTO users (nickname, password, role, first_login) VALUES
('user1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', FALSE),
('user2', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', FALSE),
('user3', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'finalUser', FALSE);

INSERT INTO month_collection_state (month, year, volunteers_availability_collection_enabled, visit_plan_created) VALUES
(6, 2025, 0, 0),
(7, 2025, 0, 0),
(8, 2025, 0, 0),
(9, 2025, 0, 0),
(10, 2025, 0, 0),
(11, 2025, 0, 0),
(12, 2025, 0, 0),
(1, 2026, 0, 0),
(2, 2026, 0, 0),
(3, 2026, 0, 0),
(4, 2026, 0, 0);

INSERT INTO config (name, value) VALUES ('max_tickets_per_user_per_event', '10');