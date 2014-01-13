--
-- Create schema cwssec
--
CREATE DATABASE IF NOT EXISTS cwssec;
COMMIT;

USE cwssec;

--
-- Source in all the sql scripts to build the tables
--
SOURCE ./cwssec.audit.sql;
SOURCE ./cwssec.groups.sql;
SOURCE ./cwssec.logon_data.sql;
SOURCE ./cwssec.reset_data.sql;
SOURCE ./cwssec.security_questions.sql;
SOURCE ./cwssec.services.sql;
SOURCE ./cwssec.users.sql;

COMMIT;

--
-- add privileges
--
GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON cwssec.* TO 'appuser'@'localhost' IDENTIFIED BY PASSWORD '*ED66694310AF846C68C9FC3D430B30594837998D';
GRANT SELECT ON mysql.proc TO 'appuser'@'localhost';

FLUSH PRIVILEGES;
COMMIT;
