DROP TABLE IF EXISTS day_tag;
CREATE TABLE day_tag
(
  day_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, tag_id)
);



