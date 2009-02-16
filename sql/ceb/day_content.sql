DROP TABLE IF EXISTS day_content;
CREATE TABLE day_content
(
  day_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, content_id)
);



