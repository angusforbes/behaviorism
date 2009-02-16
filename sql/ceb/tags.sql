DROP TABLE IF EXISTS tags;
CREATE TABLE tags
(
  tag_id int(11) AUTO_INCREMENT,
  tag VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (tag_id)
);



