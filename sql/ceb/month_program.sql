DROP TABLE IF EXISTS month_program;
CREATE TABLE month_program
(
  month_id int(11) NOT NULL,
  program_id int(11) NOT NULL,
  count int(11) NOT NULL,
  
  PRIMARY KEY(month_id, program_id)
);



