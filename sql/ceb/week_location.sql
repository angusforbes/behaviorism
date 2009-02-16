DROP TABLE IF EXISTS week_location;
CREATE TABLE week_location
(
  week_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, location_id)
);



