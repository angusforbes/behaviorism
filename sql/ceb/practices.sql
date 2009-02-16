DROP TABLE IF EXISTS practices;
CREATE TABLE practices
(
  practice_id int(11) AUTO_INCREMENT,
  practice VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (practice_id)
);



