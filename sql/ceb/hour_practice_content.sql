DROP TABLE IF EXISTS hour_practice_content;
CREATE TABLE hour_practice_content
(
  hour_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, practice_id, content_id)
);



