DROP TABLE IF EXISTS year_sector_practice;
CREATE TABLE year_sector_practice
(
  year_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (year_id, sector_id, practice_id)
);



