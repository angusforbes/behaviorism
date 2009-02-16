DROP TABLE IF EXISTS year_title;
CREATE TABLE year_title
(
  year_id int(11) NOT NULL,
  title_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, title_id)
);



