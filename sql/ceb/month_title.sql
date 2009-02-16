DROP TABLE IF EXISTS month_title;
CREATE TABLE month_title
(
  month_id int(11) NOT NULL,
  title_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, title_id)
);



