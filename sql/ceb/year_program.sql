DROP TABLE IF EXISTS year_program;
CREATE TABLE year_program
(
  year_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(year_id, program_id)
);



