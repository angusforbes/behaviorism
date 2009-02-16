DROP TABLE IF EXISTS year_sector_location;
CREATE TABLE year_sector_location
(
  year_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (year_id, sector_id, location_id)
);



