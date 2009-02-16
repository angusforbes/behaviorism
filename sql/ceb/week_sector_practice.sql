DROP TABLE IF EXISTS week_sector_practice;
CREATE TABLE week_sector_practice
(
  week_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (week_id, sector_id, practice_id)
);



