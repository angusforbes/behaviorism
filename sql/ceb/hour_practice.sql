DROP TABLE IF EXISTS hour_practice;
CREATE TABLE hour_practice
(
  hour_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, practice_id)
);



