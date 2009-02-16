DROP TABLE IF EXISTS year_tag;
CREATE TABLE year_tag
(
  year_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, tag_id)
);



