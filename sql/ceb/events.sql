DROP TABLE IF EXISTS events;
CREATE TABLE events
(
  event_id int(11) AUTO_INCREMENT,
  time TIMESTAMP NOT NULL,
  title_id int(11) NOT NULL,
  sector_id int(11) NOT NULL,
  location_id int(11) NOT NULL,
  practice_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  content_id int(11) NOT NULL,
  search_ids VARCHAR(64) NOT NULL,
  
  PRIMARY KEY (event_id)
);



