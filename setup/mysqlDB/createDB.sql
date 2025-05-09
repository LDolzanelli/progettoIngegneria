CREATE DATABASE IF NOT EXISTS ingswdb;
USE ingswdb;

CREATE TABLE area_of_interest (
  town VARCHAR(100) NOT NULL,
  PRIMARY KEY (town)
);

CREATE TABLE users (
  nickname VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  firstLogin BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (nickname),
  CHECK (role IN ('configurator', 'volunteer', 'finalUser'))
);

CREATE TABLE locations (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE location_addresses (
  street VARCHAR(100) NOT NULL,
  number VARCHAR(20) NOT NULL,
  town VARCHAR(100) NOT NULL,
  province VARCHAR(50) NOT NULL,
  location_id INT NOT NULL,
  PRIMARY KEY (street, number, town),
  FOREIGN KEY (town) REFERENCES area_of_interest(town)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (location_id) REFERENCES locations(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE visit_types (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  meetingPoint VARCHAR(100) NOT NULL,
  startDate DATE NOT NULL,
  endDate DATE NOT NULL,
  startTime TIME NOT NULL,
  duration INT NOT NULL,
  isFree BOOLEAN NOT NULL,
  minNumParticp INT NOT NULL,
  maxNumPartec INT NOT NULL,
  location_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (location_id) REFERENCES locations(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (minNumParticp >= 0),
  CHECK (maxNumPartec >= minNumParticp)
);

CREATE TABLE visits (
  id INT NOT NULL AUTO_INCREMENT,
  date DATE NOT NULL,
  volunteer_nickname VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  visit_type_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (volunteer_nickname) REFERENCES users(nickname)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (visit_type_id) REFERENCES visit_types(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE visitors (
  visit_id INT NOT NULL,
  visitor_nickname VARCHAR(50) NOT NULL,
  PRIMARY KEY (visit_id, visitor_nickname),
  FOREIGN KEY (visit_id) REFERENCES visits(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (visitor_nickname) REFERENCES users(nickname)
    ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE visit_days (
  visit_type_id INT NOT NULL,
  day_of_week VARCHAR(9) NOT NULL,
  PRIMARY KEY (visit_type_id, day_of_week),
  FOREIGN KEY (visit_type_id) REFERENCES visit_types(id) ON DELETE CASCADE,
  CHECK (day_of_week IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'))
);

