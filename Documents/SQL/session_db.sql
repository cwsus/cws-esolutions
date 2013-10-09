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
-- Create user `tomcat`
--

CREATE USER 'tomcat'@'192.168.15.%' IDENTIFIED BY PASSWORD '*BC76B32594D63CEE07D4144CBFD349B88E2FDBBB';
CREATE USER 'tomcat'@'localhost' IDENTIFIED BY PASSWORD '*BC76B32594D63CEE07D4144CBFD349B88E2FDBBB';

--
-- Create schema `session`
--

CREATE DATABASE IF NOT EXISTS `session`;
USE `session`;

--
-- Definition of table `tomcat$sessions`
--

DROP TABLE IF EXISTS `session`.`tomcat$sessions`;
CREATE TABLE `session`.`tomcat$sessions` (
  `sess_id` VARCHAR(100) NOT NULL,
  `sess_valid` CHAR(1) NOT NULL,
  `sess_maxinactive` INT(11) NOT NULL,
  `sess_lastaccess` BIGINT(20) NOT NULL,
  `app_name` VARCHAR(255) NOT NULL,
  `sess_data` MEDIUMBLOB,
  PRIMARY KEY (`sess_id`),
  KEY `kapp_name` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tomcat$sessions`
--

/*!40000 ALTER TABLE `session`.`tomcat$sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `session`.`tomcat$sessions` ENABLE KEYS */;

--
-- Grant select, insert, update, delete and execute to user
--

GRANT SELECT,INSERT,UPDATE,DELETE ON `session`.`tomcat$sessions` TO 'tomcat'@'192.168.15.%';
GRANT SELECT,INSERT,UPDATE,DELETE ON `session`.`tomcat$sessions` TO 'tomcat'@'localhost';
FLUSH PRIVILEGES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;
