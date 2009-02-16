DROP TABLE IF EXISTS months;
CREATE TABLE months
(
  month_id int(11) AUTO_INCREMENT,
  month TIMESTAMP NOT NULL UNIQUE,

  PRIMARY KEY (month_id)
);



