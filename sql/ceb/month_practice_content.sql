DROP TABLE IF EXISTS month_practice_content;
CREATE TABLE month_practice_content
(
  month_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (month_id, practice_id, content_id)
);



