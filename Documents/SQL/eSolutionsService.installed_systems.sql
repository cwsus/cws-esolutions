/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`installed_systems`
--
DROP TABLE IF EXISTS `esolutionssvc`.`installed_systems`;
CREATE TABLE `esolutionssvc`.`installed_systems` (
	`SYSTEM_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `SYSTEM_OSTYPE` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SYSTEM_STATUS` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SYSTEM_REGION` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `NETWORK_PARTITION` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `DATACENTER_GUID` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SYSTEM_TYPE` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `DOMAIN_NAME` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    `CPU_TYPE` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    `CPU_COUNT` INT NOT NULL DEFAULT 1,
    `SERVER_RACK` VARCHAR(255) CHARACTER SET UTF8,
    `RACK_POSITION` VARCHAR(255) CHARACTER SET UTF8,
    `SERVER_MODEL` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    `SERIAL_NUMBER` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
    `INSTALLED_MEMORY` INT NOT NULL,
    `OPER_IP` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT '127.0.0.1',
    `OPER_HOSTNAME` VARCHAR(100) CHARACTER SET UTF8 NOT NULL DEFAULT 'localhost.localdomain',
    `MGMT_IP` VARCHAR(50) CHARACTER SET UTF8,
    `MGMT_HOSTNAME` VARCHAR(100) CHARACTER SET UTF8,
    `BKUP_IP` VARCHAR(50) CHARACTER SET UTF8,
    `BKUP_HOSTNAME` VARCHAR(100) CHARACTER SET UTF8,
    `NAS_IP` VARCHAR(50) CHARACTER SET UTF8,
    `NAS_HOSTNAME` VARCHAR(100) CHARACTER SET UTF8,
    `NAT_ADDR` VARCHAR(50) CHARACTER SET UTF8,
    `COMMENTS` TEXT CHARACTER SET UTF8,
    `ASSIGNED_ENGINEER` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    `ADD_DATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `DELETE_DATE` TIMESTAMP,
    `DMGR_PORT` INT(5) DEFAULT 0,
    `OWNING_DMGR` VARCHAR(255),
    `MGR_ENTRY` VARCHAR(128), -- this could be a port number (for dmgr) or a url (for vmgr)
    PRIMARY KEY (`SYSTEM_GUID`),
	CONSTRAINT `FK_DATACENTER_GUID`
		FOREIGN KEY (`DATACENTER_GUID`)
		REFERENCES `esolutionssvc`.`service_datacenters` (`DATACENTER_GUID`)
			ON DELETE RESTRICT
			ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_SEARCH` (`SYSTEM_GUID`, `SYSTEM_OSTYPE`, `SYSTEM_STATUS`, `SYSTEM_REGION`, `NETWORK_PARTITION`, `DATACENTER_GUID`, `SYSTEM_TYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

--
-- Definition of procedure `esolutionssvc`.`getServerByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getServerByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getServerByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        SYSTEM_GUID,
        SYSTEM_OSTYPE,
        SYSTEM_STATUS,
        SYSTEM_REGION,
        NETWORK_PARTITION,
        DATACENTER_GUID,
        SYSTEM_TYPE,
        DOMAIN_NAME,
        CPU_TYPE,
        CPU_COUNT,
        SERVER_RACK,
        RACK_POSITION,
        SERVER_MODEL,
        SERIAL_NUMBER,
        INSTALLED_MEMORY,
        OPER_IP,
        OPER_HOSTNAME,
        MGMT_IP,
        MGMT_HOSTNAME,
        BKUP_IP,
        BKUP_HOSTNAME,
        NAS_IP,
        NAS_HOSTNAME,
        NAT_ADDR,
        COMMENTS,
        ASSIGNED_ENGINEER,
        ADD_DATE,
        DELETE_DATE,
        DMGR_PORT,
        OWNING_DMGR,
        MGR_ENTRY,
    MATCH (`SYSTEM_GUID`, `SYSTEM_OSTYPE`, `SYSTEM_STATUS`, `SYSTEM_REGION`, `NETWORK_PARTITION`, `DATACENTER_GUID`, `SYSTEM_TYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`installed_systems`
    WHERE MATCH (`SYSTEM_GUID`, `SYSTEM_OSTYPE`, `SYSTEM_STATUS`, `SYSTEM_REGION`, `NETWORK_PARTITION`, `DATACENTER_GUID`, `SYSTEM_TYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getServerByAttributeWithRegion`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getServerByAttributeWithRegion`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getServerByAttributeWithRegion`(
    IN attributeName VARCHAR(100),
    IN region VARCHAR(25),
    IN startRow INT
)
BEGIN
    SELECT
        SYSTEM_GUID,
        SYSTEM_OSTYPE,
        SYSTEM_STATUS,
        SYSTEM_REGION,
        NETWORK_PARTITION,
        DATACENTER_GUID,
        SYSTEM_TYPE,
        DOMAIN_NAME,
        CPU_TYPE,
        CPU_COUNT,
        SERVER_RACK,
        RACK_POSITION,
        SERVER_MODEL,
        SERIAL_NUMBER,
        INSTALLED_MEMORY,
        OPER_IP,
        OPER_HOSTNAME,
        MGMT_IP,
        MGMT_HOSTNAME,
        BKUP_IP,
        BKUP_HOSTNAME,
        NAS_IP,
        NAS_HOSTNAME,
        NAT_ADDR,
        COMMENTS,
        ASSIGNED_ENGINEER,
        ADD_DATE,
        DELETE_DATE,
        DMGR_PORT,
        OWNING_DMGR,
        MGR_ENTRY,
    MATCH (`SYSTEM_GUID`, `SYSTEM_OSTYPE`, `SYSTEM_STATUS`, `SYSTEM_REGION`, `NETWORK_PARTITION`, `DATACENTER_GUID`, `SYSTEM_TYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`installed_systems`
    WHERE MATCH (`SYSTEM_GUID`, `SYSTEM_OSTYPE`, `SYSTEM_STATUS`, `SYSTEM_REGION`, `NETWORK_PARTITION`, `DATACENTER_GUID`, `SYSTEM_TYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND SYSTEM_REGION = region
    ORDER BY SYSTEM_REGION ASC
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`validateServerHostName`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`validateServerHostName`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`validateServerHostName`(
    IN operHostname VARCHAR(128)
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`installed_systems`
    WHERE OPER_HOSTNAME = operHostname;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`insertNewServer`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertNewServer`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`insertNewServer`(
    IN systemGuid VARCHAR(128),
    IN systemOs VARCHAR(45),
    IN systemStatus VARCHAR(45),
    IN systemRegion VARCHAR(45),
    IN networkPartition VARCHAR(45),
    IN datacenter VARCHAR(45),
    IN systemType VARCHAR(45),
    IN domainName VARCHAR(255),
    IN cpuType VARCHAR(255),
    IN cpuCount INT,
    IN serverModel VARCHAR(255),
    IN serialNumber VARCHAR(255),
    IN installedMemory INT,
    IN operIp VARCHAR(50),
    IN operHostname VARCHAR(100),
    IN mgmtIp VARCHAR(50),
    IN mgmtHostname VARCHAR(100),
    IN backupIp VARCHAR(50),
    IN backupHostname VARCHAR(100),
    IN nasIp VARCHAR(50),
    IN nasHostname VARCHAR(100),
    IN natAddr VARCHAR(50),
    IN systemComments TEXT,
    IN engineer VARCHAR(100),
    IN mgrEntry VARCHAR(128),
    IN dmgrPort INT(5),
    IN serverRack VARCHAR(255),
    IN rackPosition VARCHAR(255),
    IN owningDmgr VARCHAR(255)
)
BEGIN
    IF dmgrPort = 0
    THEN
        INSERT INTO `esolutionssvc`.`installed_systems`
        (SYSTEM_GUID, SYSTEM_OSTYPE, SYSTEM_STATUS, SYSTEM_REGION, NETWORK_PARTITION, DATACENTER_GUID, SYSTEM_TYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, owningDmgr);
    ELSE
        INSERT INTO `esolutionssvc`.`installed_systems`
        (SYSTEM_GUID, SYSTEM_OSTYPE, SYSTEM_STATUS, SYSTEM_REGION, NETWORK_PARTITION, DATACENTER_GUID, SYSTEM_TYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, DMGR_PORT, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, dmgrPort, owningDmgr);
    END IF;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`updateServerData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateServerData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updateServerData`(
    IN systemGuid VARCHAR(128),
    IN systemOs VARCHAR(45),
    IN systemStatus VARCHAR(45),
    IN systemRegion VARCHAR(45),
    IN networkPartition VARCHAR(45),
    IN datacenter VARCHAR(45),
    IN systemType VARCHAR(45),
    IN domainName VARCHAR(255),
    IN cpuType VARCHAR(255),
    IN cpuCount INT,
    IN serverModel VARCHAR(255),
    IN serialNumber VARCHAR(255),
    IN installedMemory INT,
    IN operIp VARCHAR(50),
    IN operHostname VARCHAR(100),
    IN mgmtIp VARCHAR(50),
    IN mgmtHostname VARCHAR(100),
    IN backupIp VARCHAR(50),
    IN backupHostname VARCHAR(100),
    IN nasIp VARCHAR(50),
    IN nasHostname VARCHAR(100),
    IN natAddr VARCHAR(50),
    IN systemComments TEXT,
    IN engineer VARCHAR(100),
    IN mgrEntry VARCHAR(128),
    IN dmgrPort INT(5),
    IN serverRack VARCHAR(255),
    IN rackPosition VARCHAR(255),
    IN owningDmgr VARCHAR(255)
)
BEGIN
    UPDATE `esolutionssvc`.`installed_systems`
    SET
        SYSTEM_OSTYPE = systemOs,
        SYSTEM_STATUS = systemStatus,
        SYSTEM_REGION = systemRegion,
        NETWORK_PARTITION = networkPartition,
        DATACENTER_GUID = datacenter,
        SYSTEM_TYPE = systemType,
        DOMAIN_NAME = domainName,
        CPU_TYPE = cpuType,
        CPU_COUNT = cpuCount,
        SERVER_RACK = serverRack,
        RACK_POSITION = rackPosition,
        SERVER_MODEL = serverModel,
        SERIAL_NUMBER = serialNumber,
        INSTALLED_MEMORY = installedMemory,
        OPER_IP = operIp,
        OPER_HOSTNAME = operHostname,
        MGMT_IP = mgmtIp,
        MGMT_HOSTNAME = mgmtHostname,
        BKUP_IP = backupIp,
        BKUP_HOSTNAME = backupHostname,
        NAS_IP = nasIp,
        NAS_HOSTNAME = nasHostname,
        NAT_ADDR = natAddr,
        COMMENTS = systemComments,
        ASSIGNED_ENGINEER = engineer,
        MGR_ENTRY = mgrEntry,
        DMGR_PORT = dmgrPort,
        OWNING_DMGR = owningDmgr
    WHERE SYSTEM_GUID = systemGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`retireServer`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retireServer`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retireServer`(
    IN systemGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`installed_systems`
    SET
        SYSTEM_STATUS = 'RETIRED',
        DELETE_DATE = NOW()
    WHERE SYSTEM_GUID = systemGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `getServerCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getServerCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getServerCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`installed_systems`
    WHERE DELETE_DATE = '0000-00-00 00:00:00';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getServerList`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getServerList`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getServerList`(
    IN startRow INT
)
BEGIN
    SELECT
        SYSTEM_GUID,
        SYSTEM_OSTYPE,
        SYSTEM_STATUS,
        SYSTEM_REGION,
        NETWORK_PARTITION,
        DATACENTER_GUID,
        SYSTEM_TYPE,
        DOMAIN_NAME,
        CPU_TYPE,
        CPU_COUNT,
        SERVER_RACK,
        RACK_POSITION,
        SERVER_MODEL,
        SERIAL_NUMBER,
        INSTALLED_MEMORY,
        OPER_IP,
        OPER_HOSTNAME,
        MGMT_IP,
        MGMT_HOSTNAME,
        BKUP_IP,
        BKUP_HOSTNAME,
        NAS_IP,
        NAS_HOSTNAME,
        NAT_ADDR,
        COMMENTS,
        ASSIGNED_ENGINEER,
        ADD_DATE,
        DELETE_DATE,
        DMGR_PORT,
        OWNING_DMGR,
        MGR_ENTRY
    FROM `esolutionssvc`.`installed_systems`
    WHERE DELETE_DATE = '0000-00-00 00:00:00'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`retrServerData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrServerData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrServerData`(
    IN serverGuid VARCHAR(100)
)
BEGIN
    SELECT
        SYSTEM_GUID,
        SYSTEM_OSTYPE,
        SYSTEM_STATUS,
        SYSTEM_REGION,
        NETWORK_PARTITION,
        DATACENTER_GUID,
        SYSTEM_TYPE,
        DOMAIN_NAME,
        CPU_TYPE,
        CPU_COUNT,
        SERVER_RACK,
        RACK_POSITION,
        SERVER_MODEL,
        SERIAL_NUMBER,
        INSTALLED_MEMORY,
        OPER_IP,
        OPER_HOSTNAME,
        MGMT_IP,
        MGMT_HOSTNAME,
        BKUP_IP,
        BKUP_HOSTNAME,
        NAS_IP,
        NAS_HOSTNAME,
        NAT_ADDR,
        COMMENTS,
        ASSIGNED_ENGINEER,
        ADD_DATE,
        DELETE_DATE,
        DMGR_PORT,
        OWNING_DMGR,
        MGR_ENTRY
    FROM `esolutionssvc`.`installed_systems`
    WHERE SYSTEM_GUID = serverGuid
    AND DELETE_DATE = '0000-00-00 00:00:00';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`retrServersForDmgr`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrServersForDmgr`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrServersForDmgr`(
    IN serverGuid VARCHAR(100)
)
BEGIN
    SELECT
        SYSTEM_GUID,
        SYSTEM_OSTYPE,
        SYSTEM_STATUS,
        SYSTEM_REGION,
        NETWORK_PARTITION,
        DATACENTER_GUID,
        SYSTEM_TYPE,
        DOMAIN_NAME,
        CPU_TYPE,
        CPU_COUNT,
        SERVER_RACK,
        RACK_POSITION,
        SERVER_MODEL,
        SERIAL_NUMBER,
        INSTALLED_MEMORY,
        OPER_IP,
        OPER_HOSTNAME,
        MGMT_IP,
        MGMT_HOSTNAME,
        BKUP_IP,
        BKUP_HOSTNAME,
        NAS_IP,
        NAS_HOSTNAME,
        NAT_ADDR,
        COMMENTS,
        ASSIGNED_ENGINEER,
        ADD_DATE,
        DELETE_DATE,
        DMGR_PORT,
        OWNING_DMGR,
        MGR_ENTRY
    FROM `esolutionssvc`.`installed_systems`
    WHERE OWNING_DMGR = serverGuid
    AND DELETE_DATE = '0000-00-00 00:00:00';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Dumping data for table `esolutionssvc`.`installed_systems`
--
/*!40000 ALTER TABLE `esolutionssvc`.`installed_systems` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`installed_systems` ENABLE KEYS */;
COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;
