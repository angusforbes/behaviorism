DROP TABLE IF EXISTS day_sector;
CREATE TABLE day_sector
(
  day_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, sector_id)
);



