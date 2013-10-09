-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version    5.0.51b-community-nt-log
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

--
-- Create user `appuser`
--
-- DROP USER 'appuser'@'localhost';
-- CREATE USER 'appuser'@'localhost' IDENTIFIED BY PASSWORD '*ED66694310AF846C68C9FC3D430B30594837998D';

COMMIT;

--
-- Create schema cwssec
--
-- CREATE DATABASE IF NOT EXISTS cwssec;
-- COMMIT;

--
-- Source in all the sql scripts to build the tables
--
SOURCE ./cwssec.usr_lgn.sql;
SOURCE ./cwssec.usr_lgn_data.sql;
SOURCE ./cwssec.usr_session.sql;
SOURCE ./cwssec.usr_sec_ques.sql;
SOURCE ./cwssec.usr_audit.sql;
SOURCE ./cwssec.usr_key_data.sql;
SOURCE ./cwssec.usr_sec_roles.sql;
SOURCE ./cwssec.usr_lgn_services.sql;
SOURCE ./cwssec.usr_lgn_projects.sql;

--
-- Grant select, insert, update, delete and execute to user
--
-- GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON cwssec.* TO 'appuser'@'localhost';
-- GRANT SELECT ON `mysql`.`proc` TO 'appuser'@'localhost';
-- FLUSH PRIVILEGES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;
