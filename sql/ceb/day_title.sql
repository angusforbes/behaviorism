DROP TABLE IF EXISTS day_title;
CREATE TABLE day_title
(
  day_id int(11) NOT NULL,
  title_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, title_id)
);



