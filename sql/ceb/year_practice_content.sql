DROP TABLE IF EXISTS year_practice_content;
CREATE TABLE year_practice_content
(
  year_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,

  
  PRIMARY KEY (year_id, practice_id, content_id)
);



