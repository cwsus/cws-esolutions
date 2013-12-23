--
-- Definition of table `esolutionssvc`.`installed_applications`
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_applications`;
CREATE TABLE `esolutionssvc`.`installed_applications` (
    `APPLICATION_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `APPLICATION_NAME` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `APPLICATION_VERSION` VARCHAR(10) CHARACTER SET UTF8 NOT NULL,
    `INSTALLATION_PATH` TEXT CHARACTER SET UTF8 NOT NULL, -- where do files get installed to ?
    `CLUSTER_NAME` VARCHAR(50) CHARACTER SET UTF8, -- this only applies to webapps (and may be null if the server doesnt do clustering)
    `PACKAGE_LOCATION` TEXT CHARACTER SET UTF8, -- package location, either provided or scm'd or whatnot
    `PACKAGE_INSTALLER` TEXT CHARACTER SET UTF8, -- installer file for standalones
    `INSTALLER_OPTIONS` TEXT CHARACTER SET UTF8, -- only matters for standalone installs with an installer
    `LOGS_DIRECTORY` TEXT CHARACTER SET UTF8, -- applies only to web and standalone
    `PROJECT_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL, -- 1 project per app
    `PLATFORM_GUID` TEXT CHARACTER SET UTF8 NOT NULL, -- MULTIPLE platforms per app
    `APP_ONLINE_DATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- when did the app get added
    `APP_OFFLINE_DATE` TIMESTAMP,
    PRIMARY KEY (`APPLICATION_GUID`),
    CONSTRAINT `FK_PROJECT_GUID`
        FOREIGN KEY (`PROJECT_GUID`)
        REFERENCES `esolutionssvc`.`service_projects` (`PROJECT_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_APPLICATIONS` (`APPLICATION_NAME`, `CLUSTER_NAME`)
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
    MATCH (`APPLICATION_NAME`, `CLUSTER_NAME`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`installed_applications` T1
    INNER JOIN `esolutionssvc`.`service_projects` T2
    ON T1.PROJECT_GUID = T2.PROJECT_GUID
    WHERE MATCH (`APPLICATION_NAME`, `CLUSTER_NAME`)
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
    IN installPath TEXT,
    IN clusterName VARCHAR(50),
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN projectGuid VARCHAR(128),
    IN platformGuid VARCHAR(128)
)
BEGIN
    INSERT INTO `esolutionssvc`.`installed_applications`
    (
        APPLICATION_GUID, APPLICATION_NAME, APPLICATION_VERSION, INSTALLATION_PATH,
        CLUSTER_NAME, PACKAGE_LOCATION, PACKAGE_INSTALLER, INSTALLER_OPTIONS,
        LOGS_DIRECTORY, PROJECT_GUID, PLATFORM_GUID
    )
    VALUES
    (
        appGuid, appName, appVersion, installPath
        clusterName, packageLocation, packageInstaller, installerOptions
        logsDirectory, projectGuid, platformGuid, NOW()
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
    IN installPath TEXT,
    IN clusterName VARCHAR(50),
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN projectGuid VARCHAR(128),
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`installed_applications`
    SET
        APPLICATION_NAME = appName,
        APPLICATION_VERSION = appVersion,
        INSTALLATION_PATH = installPath,
        CLUSTER_NAME = clusterName,
        PACKAGE_LOCATION = packageLocation,
        PACKAGE_INSTALLER = packageInstaller,
        INSTALLER_OPTIONS = installerOptions,
        LOGS_DIRECTORY = logsDirectory,
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
        T1.INSTALLATION_PATH,
        T1.CLUSTER_NAME,
        T1.PACKAGE_LOCATION,
        T1.PACKAGE_INSTALLER,
        T1.INSTALLER_OPTIONS,
        T1.LOGS_DIRECTORY,
        T1.PLATFORM_GUID,
        T2.PROJECT_GUID,
        T2.PROJECT_NAME
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
