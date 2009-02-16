DROP TABLE IF EXISTS titles;
CREATE TABLE titles
(
  title_id int(11) AUTO_INCREMENT,
  title VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (title_id)
);



