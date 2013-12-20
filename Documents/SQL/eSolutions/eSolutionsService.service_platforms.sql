--
-- Definition of table `esolutionssvc`.`service_platforms`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_platforms`;
CREATE TABLE `esolutionssvc`.`service_platforms` (
    `PLATFORM_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `PLATFORM_NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `PLATFORM_REGION` VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    `PLATFORM_DMGR` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `PLATFORM_APPSERVERS` TEXT CHARACTER SET UTF8 NOT NULL,
    `PLATFORM_WEBSERVERS` TEXT CHARACTER SET UTF8 NOT NULL,
    `PLATFORM_STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `PLATFORM_DESC` TEXT,
    PRIMARY KEY (`PLATFORM_GUID`),
    CONSTRAINT `UQ_PLATFORMS` UNIQUE (`PLATFORM_DMGR`),
    FULLTEXT KEY `IDX_PLATFORMS` (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`),
    CONSTRAINT `FK_DMGR_GUID`
        FOREIGN KEY (`PLATFORM_DMGR`)
        REFERENCES `esolutionssvc`.`installed_systems` (`SYSTEM_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION
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
        PLATFORM_GUID,
        PLATFORM_NAME,
    MATCH (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_platforms`
    WHERE MATCH (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND PLATFORM_STATUS = 'ACTIVE'
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
    IN platformGuid VARCHAR(128),
    IN platformName VARCHAR(45),
    IN platformRegion VARCHAR(45),
    IN platformDmgr VARCHAR(128),
    IN platformAppservers TEXT,
    IN platformWebservers TEXT,
    IN status VARCHAR(50),
    IN platformDesc TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_platforms`
    (PLATFORM_GUID, PLATFORM_NAME, PLATFORM_REGION, PLATFORM_DMGR, PLATFORM_APPSERVERS, PLATFORM_WEBSERVERS, PLATFORM_STATUS, PLATFORM_DESC)
    VALUES
    (platformGuid, platformName, platformRegion, platformDmgr, platformAppservers, platformWebservers, status, platformDesc);

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
    IN platformGuid VARCHAR(128),
    IN platformName VARCHAR(45),
    IN platformRegion VARCHAR(45),
    IN platformDmgr VARCHAR(45),
    IN platformAppservers VARCHAR(45),
    IN platformWebservers VARCHAR(50)
)
BEGIN
    UPDATE `esolutionssvc`.`service_platforms`
    SET
        PLATFORM_NAME = platformName,
        PLATFORM_REGION = platformRegion,
        PLATFORM_DMGR = platformDmgr,
        PLATFORM_APPSERVERS = platformAppservers,
        PLATFORM_WEBSERVERS = platformWebservers
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
    SET PLATFORM_STATUS = 'INACTIVE'
    WHERE PLATFORM_GUID = platformGuid;

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
    IN platformGuid VARCHAR(128)
)
BEGIN
    SELECT
        T1.PLATFORM_GUID,
        T1.PLATFORM_NAME,
        T1.PLATFORM_REGION,
        T1.PLATFORM_APPSERVERS,
        T1.PLATFORM_WEBSERVERS,
        T1.PLATFORM_DESC,
        T2.SYSTEM_GUID,
        T2.SYSTEM_OSTYPE,
        T2.SYSTEM_STATUS,
        T2.NETWORK_PARTITION,
        T2.DOMAIN_NAME,
        T2.CPU_TYPE,
        T2.CPU_COUNT,
        T2.SERVER_RACK,
        T2.RACK_POSITION,
        T2.SERVER_MODEL,
        T2.SERIAL_NUMBER,
        T2.INSTALLED_MEMORY,
        T2.OPER_IP,
        T2.OPER_HOSTNAME,
        T2.MGMT_IP,
        T2.MGMT_HOSTNAME,
        T2.BKUP_IP,
        T2.BKUP_HOSTNAME,
        T2.NAS_IP,
        T2.NAS_HOSTNAME,
        T2.NAT_ADDR,
        T2.COMMENTS,
        T2.ASSIGNED_ENGINEER,
        T2.DMGR_PORT,
        T2.MGR_ENTRY,
        T3.DATACENTER_GUID,
        T3.DATACENTER_NAME,
        T3.DATACENTER_STATUS,
        T3.DATACENTER_DESC
    FROM `esolutionssvc`.`service_platforms` T1
    INNER JOIN `esolutionssvc`.`installed_systems` T2
    ON T1.PLATFORM_DMGR = T2.SYSTEM_GUID
    INNER JOIN `esolutionssvc`.`service_datacenters` T3
    ON T2.DATACENTER_GUID = T3.DATACENTER_GUID
    WHERE PLATFORM_GUID = platformGuid
    AND PLATFORM_STATUS = 'ACTIVE';
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
    WHERE PLATFORM_STATUS = 'ACTIVE';
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
        PLATFORM_GUID,
        PLATFORM_NAME
    FROM `esolutionssvc`.`service_platforms`
    WHERE PLATFORM_STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
