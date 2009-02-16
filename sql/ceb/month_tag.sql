DROP TABLE IF EXISTS month_tag;
CREATE TABLE month_tag
(
  month_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, tag_id)
);



