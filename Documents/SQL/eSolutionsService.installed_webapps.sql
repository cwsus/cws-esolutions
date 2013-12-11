--
-- Definition of table `esolutionssvc`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_webapps`;
CREATE TABLE `esolutionssvc`.`installed_webapps` (
    `app_uid` VARCHAR(64) CHARACTER SET UTF8 NOT NULL,
    `web_code` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `webapp_name` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `webapp_type` VARCHAR(3) CHARACTER SET UTF8 NOT NULL DEFAULT 'APP',
    `server_cluster_name` VARCHAR(45) CHARACTER SET UTF8 DEFAULT NULL,
    `force_operation` TINYINT(1) NOT NULL DEFAULT '0',
    `install_timeout` BIGINT(20) NOT NULL DEFAULT '60000',
    `web_scm_path` VARCHAR(100) CHARACTER SET UTF8 DEFAULT '',
    `web_project_directory` VARCHAR(100) CHARACTER SET UTF8 DEFAULT NULL,
    `web_logs_directory` VARCHAR(100) CHARACTER SET UTF8 DEFAULT NULL,
    `web_syslogs_directory` VARCHAR(100) CHARACTER SET UTF8 DEFAULT NULL,
    `server_version` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `entapp_description` TEXT CHARACTER SET UTF8,
    `pid_directory` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `web_online_date` DATETIME DEFAULT NULL,
    `web_offline_date` DATETIME DEFAULT NULL,
    PRIMARY KEY (`web_code`),
    FULLTEXT KEY `webapps` (`web_code`,`webapp_name`,`server_version`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

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
CREATE PROCEDURE `esolutionssvc`.`getWebappByAttribute`(
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
