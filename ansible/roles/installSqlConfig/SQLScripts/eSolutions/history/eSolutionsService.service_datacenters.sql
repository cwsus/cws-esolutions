--
-- Definition of table `esolutionssvc_hist`.`service_datacenters`
--
DROP TABLE IF EXISTS `esolutionssvc_hist`.`service_datacenters`;
CREATE TABLE `esolutionssvc_hist`.`service_datacenters` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `DESCRIPTION` TEXT CHARACTER SET UTF8,
    PRIMARY KEY (`GUID`),
    FULLTEXT KEY `IDX_DATACENTERS` (`GUID`, `NAME`, `STATUS`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc_hist`.`service_datacenters` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc_hist`.`getDataCenterByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`getDataCenterByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`getDataCenterByAttribute`(
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
    FROM `esolutionssvc_hist`.`service_datacenters`
    WHERE MATCH (`GUID`, `NAME`, `STATUS`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`addNewDatacenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`addNewDatacenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`addNewDatacenter`(
    IN datacenterGuid VARCHAR(128),
    IN datacenterName VARCHAR(45),
    IN datacenterStatus VARCHAR(45),
    IN datacenterDesc TEXT
)
BEGIN
    INSERT INTO `esolutionssvc_hist`.`service_datacenters`
    (GUID, NAME, STATUS, DESCRIPTION)
    VALUES
    (datacenterGuid, datacenterName, datacenterStatus, datacenterDesc);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`removeDataCenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`removeDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`removeDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc_hist`.`service_datacenters`
    SET STATUS = 'INACTIVE'
    WHERE GUID = datacenterGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getDatacenterCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`getDatacenterCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`getDatacenterCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_hist`.`service_datacenters`
    WHERE STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`listDataCenters`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`listDataCenters`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`listDataCenters`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `esolutionssvc_hist`.`service_datacenters`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_hist`.`retrDataCenter`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_hist`.`retrDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_hist`.`retrDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `esolutionssvc_hist`.`service_datacenters`
    WHERE GUID = datacenterGuid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
