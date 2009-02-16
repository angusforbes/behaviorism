DROP TABLE IF EXISTS hour_sector;
CREATE TABLE hour_sector
(
  hour_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, sector_id)
);



