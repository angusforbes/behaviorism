DROP TABLE IF EXISTS hour_program;
CREATE TABLE hour_program
(
  hour_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY (hour_id, program_id)
);



