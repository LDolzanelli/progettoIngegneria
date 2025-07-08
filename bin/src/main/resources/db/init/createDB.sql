USE destinazioni;

CREATE TABLE area_of_interest (
  town VARCHAR(100) NOT NULL,
  PRIMARY KEY (town)
);

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  nickname VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  first_login BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id),
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
  meeting_point VARCHAR(100) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  start_time TIME NOT NULL,
  duration INT NOT NULL,
  is_free BOOLEAN NOT NULL,
  min_num_participants INT NOT NULL,
  max_num_participants INT NOT NULL,
  location_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (location_id) REFERENCES locations(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (min_num_participants >= 0),
  CHECK (max_num_participants >= min_num_participants)
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
    ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (status IN ('proposed', 'completed', 'cancelled', 'full', 'confirmed'))
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

CREATE TABLE config (
    name VARCHAR(100) PRIMARY KEY,
    value VARCHAR(100) NOT NULL
);

