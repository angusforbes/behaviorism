DROP TABLE IF EXISTS hour_sector_practice;
CREATE TABLE hour_sector_practice
(
  hour_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, sector_id, practice_id)
);



