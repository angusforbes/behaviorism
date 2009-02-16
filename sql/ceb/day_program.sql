DROP TABLE IF EXISTS day_program;
CREATE TABLE day_program
(
  day_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(day_id, program_id)
);



