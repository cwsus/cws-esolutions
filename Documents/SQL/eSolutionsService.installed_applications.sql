--
-- Definition of table `esolutionssvc`.`installed_applications`
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_applications`;
CREATE TABLE `esolutionssvc`.`installed_applications` (
    `APPLICATION_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `APPLICATION_NAME` VARCHAR(45) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `APPLICATION_VERSION` VARCHAR(10) CHARACTER SET UTF8 NOT NULL,
    `BASE_PATH` TEXT CHARACTER SET UTF8 NOT NULL,
    `SCM_PATH` TEXT CHARACTER SET UTF8,
    `CLUSTER_NAME` VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    `JVM_NAME` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    `INSTALL_PATH` TEXT CHARACTER SET UTF8 NOT NULL,
    `LOGS_DIRECTORY` TEXT CHARACTER SET UTF8 NOT NULL,
    `PID_DIRECTORY` TEXT CHARACTER SET UTF8 NOT NULL,
    `PROJECT_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL, -- 1 project per app
    `PLATFORM_GUID` TEXT CHARACTER SET UTF8 NOT NULL, -- MULTIPLE platforms per app
    `APP_ONLINE_DATE` TIMESTAMP NOT NULL DEFAULT NOW(),
    `APP_OFFLINE_DATE` TIMESTAMP,
    PRIMARY KEY (`APPLICATION_GUID`),
    CONSTRAINT `FK_PROJECT_GUID`
        FOREIGN KEY (`PROJECT_GUID`)
        REFERENCES `esolutionssvc`.`service_projects` (`PROJECT_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_APPLICATIONS` (`APPLICATION_NAME`, `CLUSTER_NAME`, `JVM_NAME`, `PROJECT_GUID`, `PLATFORM_GUID`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`installed_applications` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getApplicationByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getApplicationByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getApplicationByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        T1.APPLICATION_GUID,
        T1.APPLICATION_NAME,
        T2.PROJECT_GUID,
        T2.PROJECT_NAME,
    MATCH (APPLICATION_NAME, CLUSTER_NAME, JVM_NAME, T1.PROJECT_GUID, PLATFORM_GUID)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`installed_applications` T1
    INNER JOIN `esolutionssvc`.`service_projects` T2
    ON T1.PROJECT_GUID = T2.PROJECT_GUID
    WHERE MATCH (APPLICATION_NAME, CLUSTER_NAME, JVM_NAME, T1.PROJECT_GUID, PLATFORM_GUID)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND APP_OFFLINE_DATE = '0000-00-00 00:00:00'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`insertNewApplication`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertNewApplication`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`insertNewApplication`(
    IN appGuid VARCHAR(128),
    IN appName VARCHAR(45),
    IN appVersion VARCHAR(10),
    IN basePath VARCHAR(128),
    IN scmPath VARCHAR(255),
    IN clusterName VARCHAR(50),
    IN jvmName VARCHAR(255),
    IN installPath VARCHAR(255),
    IN logsDir VARCHAR(255),
    IN pidDirectory VARCHAR(255),
    IN projectGuid VARCHAR(128),
    IN platformGuid VARCHAR(128)
)
BEGIN
    INSERT INTO `esolutionssvc`.`installed_applications`
    (
        APPLICATION_GUID, APPLICATION_NAME, APPLICATION_VERSION,
        BASE_PATH, SCM_PATH, CLUSTER_NAME, JVM_NAME, INSTALL_PATH,
        LOGS_DIRECTORY, PID_DIRECTORY, PROJECT_GUID,
        PLATFORM_GUID, APP_ONLINE_DATE)
    VALUES
    (
        appGuid, appName, appVersion, basePath, scmPath,
        clusterName, jvmName, installPath, logsDir,
        pidDirectory, projectGuid, platformGuid, NOW()
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`updateApplicationData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateApplicationData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`updateApplicationData`(
    IN appGuid VARCHAR(128),
    IN appName VARCHAR(45),
    IN appVersion VARCHAR(10),
    IN basePath VARCHAR(128),
    IN scmPath VARCHAR(255),
    IN clusterName VARCHAR(50),
    IN jvmName VARCHAR(255),
    IN installPath VARCHAR(255),
    IN logsDir VARCHAR(255),
    IN pidDirectory VARCHAR(255),
    IN projectGuid VARCHAR(128),
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`installed_applications`
    SET
        APPLICATION_NAME = appName,
        APPLICATION_VERSION = appVersion,
        BASE_PATH = basePath,
        SCM_PATH = scmPath,
        CLUSTER_NAME = clusterName,
        JVM_NAME = jvmName,
        INSTALL_PATH = installPath,
        LOGS_DIRECTORY = logsDir,
        PID_DIRECTORY = pidDirectory,
        PROJECT_GUID = projectGuid,
        PLATFORM_GUID = platformGuid
    WHERE APPLICATION_GUID = appGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`removeApplicationData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removeApplicationData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`removeApplicationData`(
    IN appGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`installed_applications`
    SET APP_OFFLINE_DATE = NOW()
    WHERE APPLICATION_GUID = appGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`getApplicationData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getApplicationData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getApplicationData`(
    IN appGuid VARCHAR(128)
)
BEGIN
    SELECT
        T1.APPLICATION_GUID,
        T1.APPLICATION_NAME,
        T1.APPLICATION_VERSION,
        T1.BASE_PATH,
        T1.SCM_PATH,
        T1.CLUSTER_NAME,
        T1.JVM_NAME,
        T1.INSTALL_PATH,
        T1.LOGS_DIRECTORY,
        T1.PID_DIRECTORY,
        T1.PROJECT_GUID,
        T1.PLATFORM_GUID,
        T1.APP_ONLINE_DATE,
        T1.APP_OFFLINE_DATE,
        T2.PROJECT_GUID,
        T2.PROJECT_NAME,
        T2.PROJECT_STATUS,
        T2.PRIMARY_OWNER,
        T2.SECONDARY_OWNER,
        T2.DEV_EMAIL,
        T2.PROD_EMAIL,
        T2.INCIDENT_QUEUE,
        T2.CHANGE_QUEUE
    FROM `esolutionssvc`.`installed_applications` T1
    INNER JOIN `esolutionssvc`.`service_projects` T2
    ON T1.PROJECT_GUID = T2.PROJECT_GUID
    WHERE APPLICATION_GUID = appGuid
    AND APP_OFFLINE_DATE = '0000-00-00 00:00:00';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getApplicationCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getApplicationCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getApplicationCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`installed_applications`
    WHERE APP_OFFLINE_DATE = '0000-00-00 00:00:00';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`listApplications`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listApplications`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`listApplications`(
    IN startRow INT
)
BEGIN
    SELECT
        T1.APPLICATION_GUID,
        T1.APPLICATION_NAME,
        T2.PROJECT_GUID,
        T2.PROJECT_NAME
    FROM `esolutionssvc`.`installed_applications` T1
    INNER JOIN `esolutionssvc`.`service_projects` T2
    ON T1.PROJECT_GUID = T2.PROJECT_GUID
    WHERE APP_OFFLINE_DATE = '0000-00-00 00:00:00'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;