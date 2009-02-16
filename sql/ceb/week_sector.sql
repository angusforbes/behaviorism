DROP TABLE IF EXISTS week_sector;
CREATE TABLE week_sector
(
  week_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, sector_id)
);



