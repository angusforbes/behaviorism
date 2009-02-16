DROP TABLE IF EXISTS day_sector_location;
CREATE TABLE day_sector_location
(
  day_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (day_id, sector_id, location_id)
);



