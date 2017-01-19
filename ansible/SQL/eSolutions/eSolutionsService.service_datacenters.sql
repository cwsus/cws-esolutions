--
-- Definition of table `esolutionssvc`.`service_datacenters`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_datacenters`;
CREATE TABLE `esolutionssvc`.`service_datacenters` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `DESCRIPTION` TEXT CHARACTER SET UTF8,
    PRIMARY KEY (`GUID`),
    FULLTEXT KEY `IDX_DATACENTERS` (`GUID`, `NAME`, `STATUS`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`service_datacenters` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getDataCenterByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getDataCenterByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getDataCenterByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION,
    MATCH (`GUID`, `NAME`, `STATUS`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_datacenters`
    WHERE MATCH (`GUID`, `NAME`, `STATUS`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`addNewDatacenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`addNewDatacenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`addNewDatacenter`(
    IN datacenterGuid VARCHAR(128),
    IN datacenterName VARCHAR(45),
    IN datacenterStatus VARCHAR(45),
    IN datacenterDesc TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_datacenters`
    (GUID, NAME, STATUS, DESCRIPTION)
    VALUES
    (datacenterGuid, datacenterName, datacenterStatus, datacenterDesc);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`removeDataCenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removeDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`removeDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_datacenters`
    SET STATUS = 'INACTIVE'
    WHERE GUID = datacenterGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getDatacenterCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getDatacenterCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getDatacenterCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`service_datacenters`
    WHERE STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`listDataCenters`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listDataCenters`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`listDataCenters`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `esolutionssvc`.`service_datacenters`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`retrDataCenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`retrDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `esolutionssvc`.`service_datacenters`
    WHERE GUID = datacenterGuid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
