DROP TABLE IF EXISTS year_content;
CREATE TABLE year_content
(
  year_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, content_id)
);



