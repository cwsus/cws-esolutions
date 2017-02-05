--
-- Definition of table `esolutionssvc`.`service_platforms`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_platforms`;
CREATE TABLE `esolutionssvc`.`service_platforms` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `REGION` VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    `NWPARTITION` VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    `STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `SERVERS` TEXT CHARACTER SET UTF8 NOT NULL,
    `DESCRIPTION` TEXT,
    PRIMARY KEY (`GUID`),
    FULLTEXT KEY `IDX_PLATFORMS` (`NAME`, `REGION`, `NWPARTITION`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`service_platforms` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getPlatformByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPlatformByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPlatformByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
    MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_platforms`
    WHERE MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`addNewPlatform`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`addNewPlatform`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`addNewPlatform`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_platforms` (GUID, NAME, REGION, NWPARTITION, STATUS, SERVERS, DESCRIPTION)
    VALUES (guid, name, region, nwpartition, status, servers, description);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`updatePlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updatePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`updatePlatformData`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`service_platforms`
    SET
        NAME = name,
        REGION = region,
        NWPARTITION = nwpartition,
        STATUS = status,
        SERVERS = servers,
        DESCRIPTION = description
    WHERE PROJECT_GUID = platformGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`removePlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`removePlatformData`(
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_platforms`
    SET STATUS = 'INACTIVE'
    WHERE GUID = platformGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`getPlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPlatformData`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        REGION,
        NWPARTITION,
        STATUS,
        SERVERS,
        DESCRIPTION
    FROM `esolutionssvc`.`service_platforms`
    WHERE GUID = guid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getPlatformCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPlatformCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getPlatformCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`service_platforms`
    WHERE STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`listPlatforms`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listPlatforms`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`listPlatforms`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME
    FROM `esolutionssvc`.`service_platforms`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
