DROP TABLE IF EXISTS locations;
CREATE TABLE locations
(
  location_id int(11) AUTO_INCREMENT,
  country VARCHAR(64) NOT NULL,
  state VARCHAR(64) NOT NULL,
  city VARCHAR(64) NOT NULL,
  longitude DOUBLE(9,6),
  latitude DOUBLE(9,6),

  PRIMARY KEY (location_id),
  UNIQUE(country, state, city)
);



