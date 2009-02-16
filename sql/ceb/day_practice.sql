DROP TABLE IF EXISTS day_practice;
CREATE TABLE day_practice
(
  day_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, practice_id)
);



