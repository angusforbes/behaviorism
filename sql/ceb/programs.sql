DROP TABLE IF EXISTS programs;
CREATE TABLE programs
(
  program_id int(11) AUTO_INCREMENT,
  program VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (program_id)
);



