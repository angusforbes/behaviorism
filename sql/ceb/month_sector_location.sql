DROP TABLE IF EXISTS month_sector_location;
CREATE TABLE month_sector_location
(
  month_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (month_id, sector_id, location_id)
);



