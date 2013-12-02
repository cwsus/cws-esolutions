/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_webapps`;
CREATE TABLE `esolutionssvc`.`installed_webapps` (
    `app_uid` varchar(64) NOT NULL,
    `web_code` varchar(45) NOT NULL,
    `webapp_name` varchar(45) NOT NULL,
    `webapp_type` varchar(3) NOT NULL DEFAULT 'APP',
    `server_cluster_name` varchar(45) DEFAULT NULL,
    `force_operation` tinyint(1) NOT NULL DEFAULT '0',
    `install_timeout` bigint(20) NOT NULL DEFAULT '60000',
    `web_scm_path` varchar(100) DEFAULT '',
    `web_project_directory` varchar(100) DEFAULT NULL,
    `web_logs_directory` varchar(100) DEFAULT NULL,
    `web_syslogs_directory` varchar(100) DEFAULT NULL,
    `server_version` varchar(45) NOT NULL,
    `entapp_description` text,
    `pid_directory` varchar(45) NOT NULL,
    `web_online_date` datetime DEFAULT NULL,
    `web_offline_date` datetime DEFAULT NULL,
    PRIMARY KEY (`web_code`),
    FULLTEXT KEY `webapps` (`web_code`,`webapp_name`,`server_version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `esolutionssvc`.`articles`
--
/*!40000 ALTER TABLE `esolutionssvc`.`articles` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`articles` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getWebappByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getWebappByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getWebappByAttribute`(
    IN searchTerms VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        app_uid,
        web_code,
        webapp_name,
        webapp_type,
        server_cluster_name,
        force_operation,
        install_timeout,
        web_scm_path,
        web_project_directory,
        web_logs_directory,
        web_syslogs_directory,
        server_version,
        entapp_description,
        pid_directory,
        web_online_date,
        web_offline_date,
    MATCH (`web_code`,`webapp_name`,`server_version`)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`articles`
    WHERE MATCH (`web_code`,`webapp_name`,`server_version`)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;
