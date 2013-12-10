--
-- Create schema cwssec
--
CREATE DATABASE IF NOT EXISTS cwssec;
COMMIT;

USE cwssec;

--
-- Source in all the sql scripts to build the tables
--
SOURCE ./cwssec.usr_lgn.sql;
SOURCE ./cwssec.usr_lgn_data.sql;
SOURCE ./cwssec.usr_reset_data.sql;
SOURCE ./cwssec.usr_sec_ques.sql;
SOURCE ./cwssec.usr_audit.sql;
SOURCE ./cwssec.usr_key_data.sql;
SOURCE ./cwssec.usr_sec_roles.sql;
SOURCE ./cwssec.usr_lgn_services.sql;
SOURCE ./cwssec.usr_lgn_projects.sql;

COMMIT;

--
-- add privileges
--
GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON cwssec.* TO 'appuser'@'localhost' IDENTIFIED BY PASSWORD '*ED66694310AF846C68C9FC3D430B30594837998D';
GRANT SELECT ON `mysql`.`proc` TO 'appuser'@'localhost';

FLUSH PRIVILEGES;
COMMIT;
