DROP TABLE IF EXISTS contents;
CREATE TABLE contents
(
  content_id int(11) AUTO_INCREMENT,
  content VARCHAR(64) NOT NULL UNIQUE,

  PRIMARY KEY (content_id)
);



