--
-- Definition of table `esolutionssvc`.`service_packages`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_packages`;
CREATE TABLE `esolutionssvc`.`service_packages` (
    `PACKAGE_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `PACKAGE_NAME` VARCHAR(45) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `PACKAGE_VERSION` DECIMAL(30, 2) CHARACTER SET UTF8 NOT NULL,
    `PACKAGE_LOCATION` TEXT CHARACTER SET UTF8 NOT NULL,
    `PACKAGE_INSTALLER` TEXT CHARACTER SET UTF8
    PRIMARY KEY (`PACKAGE_GUID`),
    FULLTEXT KEY `IDX_PACKAGES` (`PACKAGE_NAME`, `PACKAGE_LOCATION`, `PACKAGE_INSTALLER`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`service_packages` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getPackagesByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPackagesByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPackagesByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        PACKAGE_GUID,
        PACKAGE_NAME,
        PACKAGE_VERSION,
    MATCH (`PACKAGE_NAME`, `PACKAGE_LOCATION`, `PACKAGE_INSTALLER`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_packages`
    WHERE MATCH (`PACKAGE_NAME`, `PACKAGE_LOCATION`, `PACKAGE_INSTALLER`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`insertNewPackage`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertNewApplication`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`insertNewApplication`(
    IN packageGuid VARCHAR(128),
    IN packageName VARCHAR(45),
    IN packageVersion DECIMAL(30, 2),
    IN packageLocation VARCHAR(128),
    IN packageInstaller VARCHAR(255)
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_packages`
    (PACKAGE_GUID, PACKAGE_NAME, PACKAGE_VERSION, PACKAGE_LOCATION, PACKAGE_INSTALLER)
    VALUES
    (packageGuid, packageName, packageVersion, packageLocation, packageInstaller);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`updatePackageData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updatePackageData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`updatePackageData`(
    IN packageGuid VARCHAR(128),
    IN packageName VARCHAR(45),
    IN packageVersion DECIMAL(30, 2),
    IN packageLocation VARCHAR(128),
    IN packageInstaller VARCHAR(255)
)
BEGIN
    UPDATE `esolutionssvc`.`service_packages`
    SET
        PACKAGE_NAME = packageName,
        PACKAGE_VERSION = packageVersion,
        PACKAGE_LOCATION = packageLocation,
        PACKAGE_INSTALLER = packageInstaller
    WHERE PACKAGE_GUID = appGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`getPackageData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPackageData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPackageData`(
    IN packageGuid VARCHAR(128)
)
BEGIN
    SELECT
        PACKAGE_GUID,
        PACKAGE_NAME,
        PACKAGE_VERSION,
        PACKAGE_LOCATION,
        PACKAGE_INSTALLER
    FROM `esolutionssvc`.`service_packages` T1
    WHERE PACKAGE_GUID = packageGuid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getPackageCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPackageCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPackageCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`service_packages`;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`listPackages`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listPackages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`listPackages`(
    IN startRow INT
)
BEGIN
    SELECT
        PACKAGE_GUID,
        PACKAGE_NAME
    FROM `esolutionssvc`.`service_packages`
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;