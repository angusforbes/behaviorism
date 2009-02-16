DROP TABLE IF EXISTS week_practice_content;
CREATE TABLE week_practice_content
(
  week_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (week_id, practice_id, content_id)
);



