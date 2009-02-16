DROP TABLE IF EXISTS years;
CREATE TABLE years
(
  year_id int(11) AUTO_INCREMENT,
  year TIMESTAMP NOT NULL UNIQUE,

  PRIMARY KEY (year_id)
);



