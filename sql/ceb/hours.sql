DROP TABLE IF EXISTS hours;
CREATE TABLE hours
(
  hour_id int(11) AUTO_INCREMENT,
  hour TIMESTAMP NOT NULL UNIQUE,

  PRIMARY KEY (hour_id)
);



