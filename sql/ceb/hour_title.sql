DROP TABLE IF EXISTS hour_title;
CREATE TABLE hour_title
(
  hour_id int(11) NOT NULL,
  title_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, title_id)
);



