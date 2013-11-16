/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`service_datacenters`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_datacenters`;
CREATE TABLE `esolutionssvc`.`service_datacenters` (
    `DATACENTER_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `DATACENTER_NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `DATACENTER_STATUS` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `DATACENTER_DESC` TEXT,
    PRIMARY KEY (`DATACENTER_GUID`),
    FULLTEXT KEY `IDX_DATACENTERS` (`DATACENTER_GUID`, `DATACENTER_NAME`, `DATACENTER_STATUS`)
) ENGINE=MYISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `esolutionssvc`.`service_datacenters`
--
/*!40000 ALTER TABLE `esolutionssvc`.`service_datacenters` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`service_datacenters` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getDataCenterByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getDataCenterByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getDataCenterByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        DATACENTER_GUID,
        DATACENTER_NAME,
        DATACENTER_STATUS,
        DATACENTER_DESC,
    MATCH (`DATACENTER_GUID`, `DATACENTER_NAME`, `DATACENTER_STATUS`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_datacenters`
    WHERE MATCH (`DATACENTER_GUID`, `DATACENTER_NAME`, `DATACENTER_STATUS`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`addNewDatacenter`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`addNewDatacenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`addNewDatacenter`(
    IN datacenterGuid VARCHAR(128),
    IN datacenterName VARCHAR(45),
    IN datacenterStatus VARCHAR(45),
    IN datacenterDesc TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_datacenters`
    (DATACENTER_GUID, DATACENTER_NAME, DATACENTER_STATUS, DATACENTER_DESC)
    VALUES
    (datacenterGuid, datacenterName, datacenterStatus, datacenterDesc);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`removeDataCenter`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removeDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`removeDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_datacenters`
    SET DATACENTER_STATUS = 'INACTIVE'
    WHERE DATACENTER_GUID = datacenterGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `getDatacenterCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getDatacenterCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getDatacenterCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`service_datacenters`
    WHERE DATACENTER_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`listDataCenters`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listDataCenters`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`listDataCenters`(
    IN startRow INT
)
BEGIN
    SELECT
        DATACENTER_GUID,
        DATACENTER_NAME,
        DATACENTER_STATUS,
        DATACENTER_DESC
    FROM `esolutionssvc`.`service_datacenters`
    WHERE DATACENTER_STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrDataCenter`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrDataCenter`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    SELECT
        DATACENTER_GUID,
        DATACENTER_NAME,
        DATACENTER_STATUS,
        DATACENTER_DESC
    FROM `esolutionssvc`.`service_datacenters`
    WHERE DATACENTER_GUID = datacenterGuid
    AND DATACENTER_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;