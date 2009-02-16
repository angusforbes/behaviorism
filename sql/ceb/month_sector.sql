DROP TABLE IF EXISTS month_sector;
CREATE TABLE month_sector
(
  month_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, sector_id)
);



