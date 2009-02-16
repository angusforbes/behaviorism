DROP TABLE IF EXISTS hour_location;
CREATE TABLE hour_location
(
  hour_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, location_id)
);



