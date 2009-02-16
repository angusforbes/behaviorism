DROP TABLE IF EXISTS days;
CREATE TABLE days
(
  day_id int(11) AUTO_INCREMENT,
  day TIMESTAMP NOT NULL UNIQUE,

  PRIMARY KEY (day_id)
);



