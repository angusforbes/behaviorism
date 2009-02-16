DROP TABLE IF EXISTS month_sector_practice;
CREATE TABLE month_sector_practice
(
  month_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (month_id, sector_id, practice_id)
);



