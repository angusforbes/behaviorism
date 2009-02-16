DROP TABLE IF EXISTS day_location;
CREATE TABLE day_location
(
  day_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, location_id)
);



