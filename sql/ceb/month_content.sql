DROP TABLE IF EXISTS month_content;
CREATE TABLE month_content
(
  month_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, content_id)
);



