DROP TABLE IF EXISTS hour_sector_location;
CREATE TABLE hour_sector_location
(
  hour_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, sector_id, location_id)
);



