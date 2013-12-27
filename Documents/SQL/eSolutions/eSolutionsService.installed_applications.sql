--
-- Definition of table `esolutionssvc`.`installed_applications`
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_applications`;
CREATE TABLE `esolutionssvc`.`installed_applications` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `NAME` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `VERSION` DECIMAL(30, 2) NOT NULL DEFAULT 1.0,
    `INSTALLATION_PATH` TEXT CHARACTER SET UTF8 NOT NULL, -- where do files get installed to ?
    `PACKAGE_LOCATION` TEXT CHARACTER SET UTF8, -- package location, either provided or scm'd or whatnot
    `PACKAGE_INSTALLER` TEXT CHARACTER SET UTF8, -- installer file for standalones
    `INSTALLER_OPTIONS` TEXT CHARACTER SET UTF8, -- only matters for standalone installs with an installer
    `LOGS_DIRECTORY` TEXT CHARACTER SET UTF8, -- applies only to web and standalone
    `PLATFORM_GUID` TEXT CHARACTER SET UTF8 NOT NULL, -- MULTIPLE platforms per app
    `APP_ONLINE_DATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- when did the app get added
    `APP_OFFLINE_DATE` TIMESTAMP,
    PRIMARY KEY (`GUID`),
    FULLTEXT KEY `IDX_APPLICATIONS` (`NAME`)
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
        GUID,
        NAME,
    MATCH (`NAME`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`installed_applications`
    WHERE MATCH (`NAME`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND APP_OFFLINE_DATE IS NULL
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
    IN appVersion DECIMAL(30, 2),
    IN installPath TEXT,
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN platformGuid TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`installed_applications`
    (
        GUID, NAME, VERSION, INSTALLATION_PATH, PACKAGE_LOCATION, PACKAGE_INSTALLER,
        INSTALLER_OPTIONS, LOGS_DIRECTORY, PLATFORM_GUID, APP_ONLINE_DATE
    )
    VALUES
    (
        appGuid, appName, appVersion, installPath, packageLocation, packageInstaller,
        installerOptions, logsDirectory, platformGuid, NOW()
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
    IN appVersion DECIMAL(30, 2),
    IN installPath TEXT,
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN platformGuid TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`installed_applications`
    SET
        NAME = appName,
        VERSION = appVersion,
        INSTALLATION_PATH = installPath,
        PACKAGE_LOCATION = packageLocation,
        PACKAGE_INSTALLER = packageInstaller,
        INSTALLER_OPTIONS = installerOptions,
        LOGS_DIRECTORY = logsDirectory,
        PLATFORM_GUID = platformGuid
    WHERE GUID = appGuid;

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
    SET APP_OFFLINE_DATE = CURRENT_TIMESTAMP()
    WHERE GUID = appGuid;

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
        GUID,
        NAME,
        VERSION,
        INSTALLATION_PATH,
        PACKAGE_LOCATION,
        PACKAGE_INSTALLER,
        INSTALLER_OPTIONS,
        LOGS_DIRECTORY,
        PLATFORM_GUID
    FROM `esolutionssvc`.`installed_applications`
    WHERE GUID = appGuid
    AND APP_OFFLINE_DATE IS NULL;
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
    WHERE APP_OFFLINE_DATE IS NULL;
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
        GUID,
        NAME
    FROM `esolutionssvc`.`installed_applications`
    WHERE APP_OFFLINE_DATE IS NULL
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
