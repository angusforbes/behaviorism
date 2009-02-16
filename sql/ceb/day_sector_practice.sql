DROP TABLE IF EXISTS day_sector_practice;
CREATE TABLE day_sector_practice
(
  day_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (day_id, sector_id, practice_id)
);



