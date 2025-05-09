USE ingswdb;

-- Disabilita i vincoli di chiave esterna temporaneamente
SET FOREIGN_KEY_CHECKS = 0;

-- Elimina i dati da tutte le tabelle in ordine inverso rispetto alle dipendenze
TRUNCATE TABLE visitors;
TRUNCATE TABLE visits;
TRUNCATE TABLE visit_days;
TRUNCATE TABLE visit_types;
TRUNCATE TABLE location_addresses;
TRUNCATE TABLE locations;
TRUNCATE TABLE users;
TRUNCATE TABLE area_of_interest;

-- Riabilita i vincoli di chiave esterna
SET FOREIGN_KEY_CHECKS = 1;