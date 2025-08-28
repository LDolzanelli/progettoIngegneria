DROP DATABASE IF EXISTS destinazioni;
CREATE DATABASE IF NOT EXISTS destinazioni;
CREATE USER IF NOT EXISTS 'destinazioni'@'localhost' IDENTIFIED BY 'destinazioni';
GRANT ALL PRIVILEGES ON destinazioni.* TO 'destinazioni'@'localhost';
FLUSH PRIVILEGES;