--
-- add in the tables
--
SOURCE ./eSolutions/eSolutionsService.sql;
COMMIT;

--
-- Source in all the sql scripts to build the tables
--
SOURCE ./cwssec/cwssec.sql;
COMMIT;
