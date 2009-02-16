DROP TABLE IF EXISTS week_sector_location;
CREATE TABLE week_sector_location
(
  week_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (week_id, sector_id, location_id)
);



