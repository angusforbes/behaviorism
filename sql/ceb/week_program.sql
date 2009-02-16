DROP TABLE IF EXISTS week_program;
CREATE TABLE week_program
(
  week_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(week_id, program_id)
);



