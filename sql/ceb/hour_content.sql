DROP TABLE IF EXISTS hour_content;
CREATE TABLE hour_content
(
  hour_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, content_id)
);



