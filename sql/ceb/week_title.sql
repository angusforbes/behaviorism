DROP TABLE IF EXISTS week_title;
CREATE TABLE week_title
(
  week_id int(11) NOT NULL,
  title_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, title_id)
);



