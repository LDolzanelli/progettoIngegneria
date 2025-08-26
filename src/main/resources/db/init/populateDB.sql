USE destinazioni;

-- Inserimento degli amministratori
INSERT INTO users (nickname, password, role, first_login) VALUES
('admin1', '$2a$10$IZi.HLr26eZ8BrcDAgtoQu2gm5OPjkPMeZjWFivZAVmwXGmm6B4uC', 'configurator', FALSE),
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

INSERT INTO area_of_interest (town, province) VALUES ('Brescia', 'BS');

INSERT INTO volunteer_available_dates (volunteer_id, available_date) VALUES
(3, '2025-09-02'), (3, '2025-09-05'), (3, '2025-09-12'), (3, '2025-09-19'), (3, '2025-09-26'),
(4, '2025-09-03'), (4, '2025-09-06'), (4, '2025-09-13'), (4, '2025-09-20'), (4, '2025-09-27'),
(5, '2025-09-04'), (5, '2025-09-07'), (5, '2025-09-14'), (5, '2025-09-21'), (5, '2025-09-28');

INSERT INTO blocked_dates (date) VALUES
('2025-09-08'),
('2025-09-22');

INSERT INTO config (name, value) VALUES ('max_tickets_per_user_per_event', '10');

INSERT INTO locations (name, description) VALUES
('Museo di Santa Giulia', 'Museo della città di Brescia, patrimonio UNESCO.'),
('Castello di Brescia', 'Fortezza medievale che domina la città.');

INSERT INTO location_addresses (street, number, town, province, location_id) VALUES
('Via dei Musei', '81', 'Brescia', 'BS', 1),
('Via del Castello', '9', 'Brescia', 'BS', 2);

INSERT INTO visit_types (title, description, meeting_point, start_date, end_date, start_time, duration, is_free, min_num_participants, max_num_participants, location_id)
VALUES
('Visita guidata a Santa Giulia - Romanico e Rinascimento',
 'Percorso tra le opere medievali e rinascimentali del museo.',
 'Ingresso principale Museo di Santa Giulia',
 '2025-09-01', '2025-09-30', '10:00:00', 120, FALSE, 5, 25, 1),
('Mostra su Giacomo Ceruti - "Pitocchetto"',
 'Scopri le opere del grande pittore bresciano del Settecento.',
 'Ingresso principale Museo di Santa Giulia',
 '2025-09-01', '2025-09-30', '15:00:00', 90, FALSE, 3, 20, 1),
('Visita al Castello di Brescia - Archeologia e Armi',
 'Tour tra le collezioni di armi e la storia della fortezza.',
     'Piazzale ingresso del Castello',
 '2025-09-01', '2025-09-30', '11:00:00', 120, TRUE, 5, 30, 2),
('Visita al Museo del Risorgimento',
 'Percorso dedicato al Risorgimento italiano nel Castello.',
 'Ingresso Museo del Risorgimento',
 '2025-09-01', '2025-09-30', '16:00:00', 90, FALSE, 3, 20, 2);

INSERT INTO visit_days (visit_type_id, day_of_week) VALUES
(1, 'Tuesday'), (1, 'Thursday'), (1, 'Saturday'),
(2, 'Friday'), (2, 'Sunday'),
(3, 'Wednesday'), (3, 'Saturday'),
(4, 'Monday'), (4, 'Thursday');

INSERT INTO volunteers_visit_types (visit_type_id, volunteer_id) VALUES
(1, 2), (1, 3),
(2, 2),
(3, 3),
(4, 2), (4, 3);

INSERT INTO month_collection_state (month, year, volunteers_availability_collection_enabled, visit_plan_created) VALUES
(8, 2025, 1, 0),
(9, 2025, 1, 0);
