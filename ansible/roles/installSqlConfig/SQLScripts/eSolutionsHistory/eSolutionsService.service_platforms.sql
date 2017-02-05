--
-- Definition of table `esolutionssvc_hist`.`service_platforms`
--
DROP TABLE IF EXISTS `esolutionssvc_hist`.`service_platforms`;
CREATE TABLE `esolutionssvc_hist`.`service_platforms` (
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

ALTER TABLE `esolutionssvc_hist`.`service_platforms` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc_hist`.`getPlatformByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`getPlatformByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`getPlatformByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
    MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc_hist`.`service_platforms`
    WHERE MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`addNewPlatform`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`addNewPlatform`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`addNewPlatform`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    INSERT INTO `esolutionssvc_hist`.`service_platforms` (GUID, NAME, REGION, NWPARTITION, STATUS, SERVERS, DESCRIPTION)
    VALUES (guid, name, region, nwpartition, status, servers, description);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`updatePlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`updatePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`updatePlatformData`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    UPDATE `esolutionssvc_hist`.`service_platforms`
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
-- Definition of procedure `esolutionssvc_hist`.`removePlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`removePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`removePlatformData`(
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc_hist`.`service_platforms`
    SET STATUS = 'INACTIVE'
    WHERE GUID = platformGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`getPlatformData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`getPlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`getPlatformData`(
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
    FROM `esolutionssvc_hist`.`service_platforms`
    WHERE GUID = guid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getPlatformCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`getPlatformCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`getPlatformCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_hist`.`service_platforms`
    WHERE STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`listPlatforms`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`listPlatforms`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`listPlatforms`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME
    FROM `esolutionssvc_hist`.`service_platforms`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
