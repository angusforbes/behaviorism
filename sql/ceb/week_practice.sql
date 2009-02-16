DROP TABLE IF EXISTS week_practice;
CREATE TABLE week_practice
(
  week_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, practice_id)
);



