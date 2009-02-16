DROP TABLE IF EXISTS week_content;
CREATE TABLE week_content
(
  week_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, content_id)
);



