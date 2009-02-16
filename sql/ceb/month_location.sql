DROP TABLE IF EXISTS month_location;
CREATE TABLE month_location
(
  month_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, location_id)
);



