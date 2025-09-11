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
(8, 2025, 0, 1),
(9, 2025, 0, 0),
(10, 2025, 0, 0);

INSERT INTO config (name, value) VALUES ('max_tickets_per_user_per_event', '10');

INSERT INTO area_of_interest (town, province) VALUES ('Brescia', 'BS');

INSERT INTO volunteer_available_dates (volunteer_id, available_date) VALUES
(3, '2025-09-02'), (3, '2025-09-05'), (3, '2025-09-12'), (3, '2025-09-19'), (3, '2025-09-26'),
(3, '2025-09-04'), (3, '2025-09-07'), (3, '2025-09-18'), (3, '2025-09-21'), (3, '2025-09-30'),
(4, '2025-09-03'), (4, '2025-09-06'), (4, '2025-09-13'), (4, '2025-09-20'), (4, '2025-09-27'),
(4, '2025-09-01'), (4, '2025-09-05'), (4, '2025-09-14'), (4, '2025-09-22'), (4, '2025-09-28'),
(5, '2025-09-04'), (5, '2025-09-07'), (5, '2025-09-14'), (5, '2025-09-21'), (5, '2025-09-28'),
(5, '2025-09-02'), (5, '2025-09-10'), (5, '2025-09-16'), (5, '2025-09-22'), (5, '2025-09-29');

INSERT INTO blocked_dates (date) VALUES
('2025-09-08'),
('2025-09-22');

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
 '2025-08-01', '2025-10-31', '10:00:00', 120, FALSE, 5, 25, 1),
('Mostra su Giacomo Ceruti - "Pitocchetto"',
 'Scopri le opere del grande pittore bresciano del Settecento.',
 'Ingresso principale Museo di Santa Giulia',
 '2025-08-01', '2025-10-31', '15:00:00', 90, FALSE, 3, 20, 1),
('Visita al Castello di Brescia - Archeologia e Armi',
 'Tour tra le collezioni di armi e la storia della fortezza.',
     'Piazzale ingresso del Castello',
 '2025-08-01', '2025-10-31', '11:00:00', 120, TRUE, 5, 30, 2),
('Visita al Museo del Risorgimento',
 'Percorso dedicato al Risorgimento italiano nel Castello.',
 'Ingresso Museo del Risorgimento',
 '2025-08-01', '2025-10-31', '16:00:00', 90, FALSE, 3, 20, 2);

INSERT INTO visit_days (visit_type_id, day_of_week) VALUES
(1, 'Tuesday'), (1, 'Thursday'), (1, 'Saturday'),
(2, 'Friday'), (2, 'Sunday'),
(3, 'Wednesday'), (3, 'Saturday'),
(4, 'Monday'), (4, 'Thursday');

INSERT INTO volunteers_visit_types (visit_type_id, volunteer_id) VALUES
(1, 3), (1, 4),
(2, 3), (2, 5),
(3, 4), (3, 5),
(4, 3), (4, 4), (4, 5);

INSERT INTO month_collection_state (month, year, volunteers_availability_collection_enabled, visit_plan_created) VALUES
(8, 2025, 0, 1),
(9, 2025, 0, 0),
(10, 2025, 0, 0);

-- valori che sono generati dal sistema quando viene aperta la raccolta disponibilità di settembre. aggiunti manualmente
-- per comodità di testing
INSERT INTO visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(1, '2025-09-05', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(2, '2025-09-07', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(3, '2025-09-12', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(4, '2025-09-14', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(5, '2025-09-19', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(6, '2025-09-21', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(7, '2025-09-26', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(8, '2025-09-28', NULL, 'PROPOSED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(9, '2025-09-03', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(10, '2025-09-06', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(11, '2025-09-10', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(12, '2025-09-13', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(13, '2025-09-17', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(14, '2025-09-20', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(15, '2025-09-24', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(16, '2025-09-27', NULL, 'PROPOSED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(17, '2025-09-01', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(18, '2025-09-04', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(19, '2025-09-08', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(20, '2025-09-11', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(21, '2025-09-15', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(22, '2025-09-18', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(23, '2025-09-22', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(24, '2025-09-25', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(25, '2025-09-29', NULL, 'PROPOSED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(26, '2025-09-02', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(27, '2025-09-04', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(28, '2025-09-06', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(29, '2025-09-09', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(30, '2025-09-11', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(31, '2025-09-13', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(32, '2025-09-16', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(33, '2025-09-18', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(34, '2025-09-20', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(35, '2025-09-23', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(36, '2025-09-25', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(37, '2025-09-27', NULL, 'PROPOSED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(38, '2025-09-30', NULL, 'PROPOSED', 1);


-- Visite per archivio storico con stato "completed"


INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(39, '2025-08-02', 3, 'COMPLETED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(40, '2025-08-05', 5, 'COMPLETED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(41, '2025-08-08', 4, 'COMPLETED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(43, '2025-08-14', 4, 'COMPLETED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(44, '2025-08-10', 3, 'COMPLETED', 2);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(45, '2025-08-11', 5, 'COMPLETED', 3);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(46, '2025-08-12', 4, 'COMPLETED', 4);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(48, '2025-08-04', 5, 'COMPLETED', 2);

-- Visite in stato "Confirmed" per volontario-marco

INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(47, '2025-08-17', 3, 'CONFIRMED', 1);
INSERT INTO destinazioni.visits
(id, `date`, volunteer_id, status, visit_type_id)
VALUES(42, '2025-08-18', 3, 'CONFIRMED', 4);

-- Aggiunta di booking per far risultare che c'erano effettivamente prenotazioni
-- Visit 39 (6 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Marco', 39, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Giovanni', 39, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Luca', 39, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Giorgia', 39, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Lorenzo', 39, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG39X', 'Alessandra', 39, 6);

-- Visit 40 (5 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG40Y', 'Anna', 40, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG40Y', 'Paolo', 40, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG40Y', 'Sara', 40, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG40Y', 'Matteo', 40, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG40Y', 'Chiara', 40, 6);

-- Visit 41 (8 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Elena', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Francesco', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Davide', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Martina', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Alessio', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Federica', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Simone', 41, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG41Z', 'Laura', 41, 6);

-- Visit 42 (7 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG42A', 'Giulia', 42, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG42A', 'Simone', 42, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG42A', 'Federico', 42, 6);
INSERT INTO destinazioni.bookings VALUES ('GHFB74', 'Laura', 42, 7);
INSERT INTO destinazioni.bookings VALUES ('GHFB74', 'Andrea', 42, 7);
INSERT INTO destinazioni.bookings VALUES ('MHWL38', 'Elisa', 42, 8);
INSERT INTO destinazioni.bookings VALUES ('MHWL38', 'Roberto', 42, 8);

-- Visit 43 (5 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG43B', 'Stefano', 43, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG43B', 'Marta', 43, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG43B', 'Carlo', 43, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG43B', 'Elisa', 43, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG43B', 'Roberto', 43, 6);

-- Visit 44 (9 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Valentina', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Emanuele', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Claudia', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Nicola', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Serena', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Filippo', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Roberta', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Lorenzo', 44, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG44C', 'Michela', 44, 6);

-- Visit 45 (6 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Fabio', 45, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Gabriele', 45, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Veronica', 45, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Ludovica', 45, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Pietro', 45, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG45D', 'Chiara', 45, 6);

-- Visit 46 (10 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Angela', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Tommaso', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Cristina', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Giuseppe', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Silvia', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Alessandro', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Monica', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Riccardo', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Elena', 46, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG46E', 'Federico', 46, 6);

-- Visit 47 (10 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG47F', 'Riccardo', 47, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG47F', 'Beatrice', 47, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG47F', 'Daniele', 47, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG47F', 'Federica', 47, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG47F', 'Antonio', 47, 6);
INSERT INTO destinazioni.bookings VALUES ('AFHA31', 'Monica', 47, 7);
INSERT INTO destinazioni.bookings VALUES ('53GEP2', 'Elena', 47, 8);
INSERT INTO destinazioni.bookings VALUES ('53GEP2', 'Lorenzo', 47, 8);
INSERT INTO destinazioni.bookings VALUES ('CN351A', 'Michele', 47, 8);
INSERT INTO destinazioni.bookings VALUES ('CN351A', 'Ilaria', 47, 8);

-- Visit 48 (7 visitors)
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Michele', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Ilaria', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Lorenzo', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Caterina', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Alberto', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Martina', 48, 6);
INSERT INTO destinazioni.bookings VALUES ('AUG48G', 'Stefano', 48, 6);