--
-- Definition of table `esolutionssvc_history`.`installed_systems`
--
DROP TABLE IF EXISTS `esolutionssvc_history`.`installed_systems`;
CREATE TABLE `esolutionssvc_history`.`installed_systems` (
    `GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `SYSTEM_OSTYPE` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `STATUS` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `REGION` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `NWPARTITION` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `DATACENTER_GUID` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SYSTYPE` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
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
    `OWNING_DMGR` VARCHAR(255) CHARACTER SET UTF8,
    `MGR_ENTRY` VARCHAR(128) CHARACTER SET UTF8, -- this could be a port number (for dmgr) or a url (for vmgr)
    PRIMARY KEY (`GUID`),
    CONSTRAINT `FK_DATACENTER_GUID`
        FOREIGN KEY (`DATACENTER_GUID`)
        REFERENCES `esolutionssvc_history`.`service_datacenters` (`DATACENTER_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_SEARCH` (`GUID`, `SYSTEM_OSTYPE`, `STATUS`, `REGION`, `NWPARTITION`, `DATACENTER_GUID`, `SYSTYPE`, `OPER_HOSTNAME`, `ASSIGNED_ENGINEER`, `OWNING_DMGR`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc_history`.`installed_systems` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc_history`.`getServerByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServerByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServerByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        T1.GUID,
        T1.REGION,
        T1.NWPARTITION,
        T1.OPER_HOSTNAME,
        T2.GUID,
        T2.NAME,
    MATCH (T1.GUID, T1.SYSTEM_OSTYPE, T1.STATUS, T1.REGION, T1.NWPARTITION, T1.DATACENTER_GUID, T1.SYSTYPE, T1.OPER_HOSTNAME, T1.ASSIGNED_ENGINEER, T1.OWNING_DMGR)
    AGAINST (attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc_history`.`installed_systems` T1
    INNER JOIN `esolutionssvc_history`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE MATCH (T1.GUID, T1.SYSTEM_OSTYPE, T1.STATUS, T1.REGION, T1.NWPARTITION, T1.DATACENTER_GUID, T1.SYSTYPE, T1.OPER_HOSTNAME, T1.ASSIGNED_ENGINEER, T1.OWNING_DMGR)
    AGAINST (attributeName IN BOOLEAN MODE)
    AND DELETE_DATE IS NULL
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`validateServerHostName`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`validateServerHostName`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`validateServerHostName`(
    IN operHostname VARCHAR(128)
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_history`.`installed_systems`
    WHERE OPER_HOSTNAME = operHostname;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`insertNewServer`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`insertNewServer`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`insertNewServer`(
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
        INSERT INTO `esolutionssvc_history`.`installed_systems`
        (GUID, SYSTEM_OSTYPE, STATUS, REGION, NWPARTITION, DATACENTER_GUID, SYSTYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, owningDmgr);
    ELSE
        INSERT INTO `esolutionssvc_history`.`installed_systems`
        (GUID, SYSTEM_OSTYPE, STATUS, REGION, NWPARTITION, DATACENTER_GUID, SYSTYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, DMGR_PORT, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, dmgrPort, owningDmgr);
    END IF;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`updateServerData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`updateServerData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`updateServerData`(
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
    UPDATE `esolutionssvc_history`.`installed_systems`
    SET
        SYSTEM_OSTYPE = systemOs,
        STATUS = systemStatus,
        REGION = systemRegion,
        NWPARTITION = networkPartition,
        DATACENTER_GUID = datacenter,
        SYSTYPE = systemType,
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
    WHERE GUID = systemGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`retireServer`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retireServer`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`retireServer`(
    IN systemGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc_history`.`installed_systems`
    SET
        STATUS = 'RETIRED',
        DELETE_DATE = CURRENT_TIMESTAMP()
    WHERE GUID = systemGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getServerCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServerCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServerCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_history`.`installed_systems`
    WHERE DELETE_DATE IS NULL;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`getServerList`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getServerList`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getServerList`(
    IN startRow INT
)
BEGIN
    SELECT
        T1.GUID,
        T1.REGION,
        T1.NWPARTITION,
        T1.OPER_HOSTNAME,
        T1.OWNING_DMGR,
        T2.GUID,
        T2.NAME
    FROM `esolutionssvc_history`.`installed_systems`
    INNER JOIN `esolutionssvc_history`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE DELETE_DATE IS NULL
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`retrServerData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retrServerData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`retrServerData`(
    IN serverGuid VARCHAR(128)
)
BEGIN
    SELECT
        T1.GUID,
        T1.SYSTEM_OSTYPE,
        T1.STATUS,
        T1.REGION,
        T1.NWPARTITION,
        T1.SYSTYPE,
        T1.DOMAIN_NAME,
        T1.CPU_TYPE,
        T1.CPU_COUNT,
        T1.SERVER_RACK,
        T1.RACK_POSITION,
        T1.SERVER_MODEL,
        T1.SERIAL_NUMBER,
        T1.INSTALLED_MEMORY,
        T1.OPER_IP,
        T1.OPER_HOSTNAME,
        T1.MGMT_IP,
        T1.MGMT_HOSTNAME,
        T1.BKUP_IP,
        T1.BKUP_HOSTNAME,
        T1.NAS_IP,
        T1.NAS_HOSTNAME,
        T1.NAT_ADDR,
        T1.COMMENTS,
        T1.ASSIGNED_ENGINEER,
        T1.ADD_DATE,
        T1.DELETE_DATE,
        T1.DMGR_PORT,
        T1.OWNING_DMGR,
        T1.MGR_ENTRY,
        T2.GUID,
        T2.NAME
    FROM `esolutionssvc_history`.`installed_systems` T1
    INNER JOIN `esolutionssvc_history`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE T1.GUID = serverGuid
    AND DELETE_DATE IS NULL;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getRetiredServers`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getRetiredServers`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getRetiredServers`(
)
BEGIN
    SELECT GUID
    FROM `esolutionssvc_history`.`installed_systems`
    WHERE DELETE_DATE IS NOT NULL;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getRetiredServers`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retireServer`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`retireServer`(
    IN guid VARCHAR(128)
)
BEGIN
    INSERT INTO esolutionssvc_history_hist.installed_systems
    SELECT *
    FROM esolutionssvc_history.installed_systems
    WHERE GUID = guid
    AND DELETE_DATE IS NOT NULL;

    COMMIT;

    DELETE FROM esolutionssvc_history.installed_systems
    WHERE GUID = guid
    AND DELETE_DATE IS NOT NULL;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
