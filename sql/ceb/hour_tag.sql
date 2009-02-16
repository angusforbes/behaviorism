DROP TABLE IF EXISTS hour_tag;
CREATE TABLE hour_tag
(
  hour_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(hour_id, tag_id)
);



