--
-- Definition of table `esolutionssvc_history`.`services`
--
DROP TABLE IF EXISTS `esolutionssvc_history`.`services`;
CREATE TABLE `esolutionssvc_history`.`services` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `SERVICE_TYPE` VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    `NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    `REGION` VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    `NWPARTITION` VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    `STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `SERVERS` TEXT CHARACTER SET UTF8,
    `DESCRIPTION` TEXT,
    PRIMARY KEY (`GUID`),
    FULLTEXT KEY `IDX_PLATFORMS` (`SERVICE_TYPE`, `NAME`, `REGION`, `NWPARTITION`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc_history`.`services` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc_history`.`getServiceByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServiceByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServiceByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        SERVICE_TYPE,
    MATCH (`SERVICE_TYPE`, `NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc_history`.`services`
    WHERE MATCH (`SERVICE_TYPE`, `NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`addNewService`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`addNewService`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`addNewService`(
    IN guid VARCHAR(128),
    IN serviceType VARCHAR(50),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    INSERT INTO `esolutionssvc_history`.`services` (GUID, SERVICE_TYPE, NAME, REGION, NWPARTITION, STATUS, SERVERS, DESCRIPTION)
    VALUES (guid, serviceType, name, region, nwpartition, status, servers, description);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`updateServiceData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`updateServiceData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`updateServiceData`(
    IN guid VARCHAR(128),
    IN serviceType VARCHAR(50),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    UPDATE `esolutionssvc_history`.`services`
    SET
        SERVICE_TYPE = serviceType,
        NAME = name,
        REGION = region,
        NWPARTITION = nwpartition,
        STATUS = status,
        SERVERS = servers,
        DESCRIPTION = description
    WHERE GUID = guid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`removeServiceData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`removeServiceData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`removeServiceData`(
    IN guid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc_history`.`services`
    SET STATUS = 'INACTIVE'
    WHERE GUID = guid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getServiceCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServiceCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServiceCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_history`.`services`
    WHERE STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`listServices`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`listServices`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`listServices`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        SERVICE_TYPE,
        NAME
    FROM `esolutionssvc_history`.`services`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`getServiceData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServiceData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServiceData`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        SERVICE_TYPE,
        NAME,
        REGION,
        NWPARTITION,
        STATUS,
        SERVERS,
        DESCRIPTION
    FROM `esolutionssvc_history`.`services`
    WHERE GUID = guid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;

