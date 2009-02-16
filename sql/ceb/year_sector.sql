DROP TABLE IF EXISTS year_sector;
CREATE TABLE year_sector
(
  year_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, sector_id)
);



