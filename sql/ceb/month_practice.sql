DROP TABLE IF EXISTS month_practice;
CREATE TABLE month_practice
(
  month_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, practice_id)
);



