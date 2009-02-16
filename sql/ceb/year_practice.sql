DROP TABLE IF EXISTS year_practice;
CREATE TABLE year_practice
(
  year_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, practice_id)
);



