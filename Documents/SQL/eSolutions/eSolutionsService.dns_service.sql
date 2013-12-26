--
-- Definition of table `esolutionssvc`.`dns_service`
--
DROP TABLE IF EXISTS `esolutionssvc`.`dns_service`;
CREATE TABLE `esolutionssvc`.`dns_service` (
    `ID` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `ZONE_FILE` VARCHAR(128) CHARACTER SET UTF8 NOT NULL, -- required for all entries, this will act as a correlator for apex/sub
    `APEX_RECORD` BOOLEAN NOT NULL DEFAULT FALSE,
    `ORIGIN` VARCHAR(126) CHARACTER SET UTF8 NOT NULL DEFAULT ".", -- required for all entries
    `TIMETOLIVE` INTEGER, -- required for apex records and srv records (we're going to re-use it)
    `HOSTNAME` VARCHAR(126) CHARACTER SET UTF8 NOT NULL, -- required for apex records - 63 per label, including TLD this is 126
    `OWNER` VARCHAR(255) CHARACTER SET UTF8, -- required for apex records
    `HOSTMASTER` VARCHAR(255) CHARACTER SET UTF8, -- required for apex records
    `SERIAL` INTEGER(11), -- required for apex records
    `REFRESH` INTEGER, -- required for apex records
    `RETRY` INTEGER, -- required for apex records
    `EXPIRES` INTEGER, -- required for apex records
    `CACHETIME` INTEGER, -- required for apex records
    `CLASS_NAME` VARCHAR(8) CHARACTER SET UTF8 NOT NULL DEFAULT "IN", -- required for all records
    `CLASS_TYPE` VARCHAR(11) CHARACTER SET UTF8, -- required for all records
    `PORT` INTEGER(6), -- required for srv records
    `WEIGHT` INTEGER(3), -- required for srv and mx records
    `SERVICE` VARCHAR(10) CHARACTER SET UTF8, -- required for srv records
    `PROTOCOL` VARCHAR(6) CHARACTER SET UTF8, -- required for srv records
    `PRIORITY` INTEGER(3) DEFAULT 10, -- required for srv records
    `PRIMARY_TARGET` VARCHAR(255) CHARACTER SET UTF8, -- required for all records
    `SECONDARY_TARGET` VARCHAR(255) CHARACTER SET UTF8, -- secondary target list, used for failover
    `TERTIARY_TARGET` VARCHAR(255) CHARACTER SET UTF8, -- tertiary target list, used for failover
    PRIMARY KEY (`ID`),
    FULLTEXT KEY `IDX_SEARCH` (`ZONE_FILE`, `ORIGIN`, `HOSTNAME`, `OWNER`, `CLASS_TYPE`, `SERVICE`, `PRIMARY_TARGET`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

ALTER TABLE `esolutionssvc`.`dns_service` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getRecordByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getRecordByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getRecordByAttribute`(
    IN attributeName VARCHAR(126)
)
BEGIN
    SELECT
        ZONE_FILE,
        APEX_RECORD,
        ORIGIN,
        TIMETOLIVE,
        HOSTNAME,
        OWNER,
        HOSTMASTER,
        SERIAL,
        REFRESH,
        RETRY,
        EXPIRES,
        CACHETIME,
        CLASS_NAME,
        CLASS_TYPE,
        PORT,
        WEIGHT,
        SERVICE,
        PROTOCOL,
        PRIORITY,
        PRIMARY_TARGET,
        SECONDARY_TARGET,
        TERTIARY_TARGET,
    MATCH (`ZONE_FILE`, `ORIGIN`, `HOSTNAME`, `OWNER`, `CLASS_TYPE`, `SERVICE`, `PRIMARY_TARGET`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`dns_service`
    WHERE MATCH (`ZONE_FILE`, `ORIGIN`, `HOSTNAME`, `OWNER`, `CLASS_TYPE`, `SERVICE`, `PRIMARY_TARGET`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    ORDER BY APEX_RECORD DESC, ORIGIN ASC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$ 

--
-- Definition of procedure `esolutionssvc`.`insertApex`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertApex`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`insertApex`(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN timeToLive INTEGER(12),
    IN hostname VARCHAR(126),
    IN masterNameserver VARCHAR(255),
    IN hostmaster VARCHAR(255),
    IN serial INTEGER(11),
    IN refresh INTEGER(12),
    IN retry INTEGER(12),
    IN expiry INTEGER(12),
    IN cacheTime INTEGER(12)
)
BEGIN
    INSERT INTO `esolutionssvc`.`dns_service`
    (
        ZONE_FILE, APEX_RECORD, ORIGIN, 
        TIMETOLIVE, HOSTNAME, OWNER, HOSTMASTER, 
        SERIAL, REFRESH, RETRY, EXPIRES, CACHETIME
    )
    VALUES
    (
        zoneFile, true, origin, timeToLive,
        hostname, masterNameserver, hostmaster, serial,
        refresh, retry, expiry, cacheTime
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`insertRecord`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertRecord`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`insertRecord`(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN hostname VARCHAR(126),
    IN rrClass VARCHAR(8),
    IN rrType VARCHAR(11),
    IN portNumber INTEGER(6),
    IN weight INTEGER(6),
    IN service VARCHAR(10),
    IN protocol VARCHAR(6),
    IN priority INTEGER(6),
    IN target VARCHAR(255),
    IN secondary VARCHAR(255),
    IN tertiary VARCHAR(255)
)
BEGIN
    INSERT INTO `esolutionssvc`.`dns_service`
    (
        ZONE_FILE, APEX_RECORD, ORIGIN,
        HOSTNAME, CLASS_NAME, CLASS_TYPE, PORT, WEIGHT,
        SERVICE, PROTOCOL, PRIORITY, PRIMARY_TARGET,
        SECONDARY_TARGET, TERTIARY_TARGET
    )
    VALUES
    (
        zoneFile, false, origin,
        hostname, rrClass, rrType, portNumber,
        weight, service, protocol, priority, target,
        secondary, tertiary
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$ 

DELIMITER ;
COMMIT;
