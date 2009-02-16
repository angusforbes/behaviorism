DROP TABLE IF EXISTS year_location;
CREATE TABLE year_location
(
  year_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, location_id)
);



