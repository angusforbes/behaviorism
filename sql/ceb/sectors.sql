DROP TABLE IF EXISTS sectors;
CREATE TABLE sectors
(
  sector_id int(11) AUTO_INCREMENT,
  sector VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (sector_id)
);



