DROP TABLE IF EXISTS week_tag;
CREATE TABLE week_tag
(
  week_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, tag_id)
);



