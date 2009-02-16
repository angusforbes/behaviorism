DROP TABLE IF EXISTS day_practice_content;
CREATE TABLE day_practice_content
(
  day_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (day_id, practice_id, content_id)
);



