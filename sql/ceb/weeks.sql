DROP TABLE IF EXISTS weeks;
CREATE TABLE weeks
(
  week_id int(11) AUTO_INCREMENT,
  week TIMESTAMP NOT NULL UNIQUE,

  PRIMARY KEY (week_id)
);



