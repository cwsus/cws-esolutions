--
-- Definition of table esolutionssvc.services
--
DROP TABLE IF EXISTS esolutionssvc.services;
CREATE TABLE esolutionssvc.services (
    GUID VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    SERVICE_TYPE VARCHAR(50) CHARACTER SET UTF8 NOT NULL,
    NAME VARCHAR(128) CHARACTER SET UTF8 NOT NULL UNIQUE,
    REGION VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    NWPARTITION VARCHAR(15) CHARACTER SET UTF8 NOT NULL,
    STATUS VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    SERVERS TEXT CHARACTER SET UTF8,
    DESCRIPTION TEXT,
    PRIMARY KEY (GUID),
    FULLTEXT KEY IDX_PLATFORMS (SERVICE_TYPE, NAME, REGION, NWPARTITION)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE esolutionssvc.services CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure esolutionssvc.getServiceByAttribute
--
DROP PROCEDURE IF EXISTS esolutionssvc.getServiceByAttribute$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.getServiceByAttribute(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        SERVICE_TYPE,
    MATCH (SERVICE_TYPE, NAME, REGION, NWPARTITION)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM esolutionssvc.services
    WHERE MATCH (SERVICE_TYPE, NAME, REGION, NWPARTITION)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc.addNewService
--
DROP PROCEDURE IF EXISTS esolutionssvc.addNewService$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.addNewService(
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
    INSERT INTO esolutionssvc.services (GUID, SERVICE_TYPE, NAME, REGION, NWPARTITION, STATUS, SERVERS, DESCRIPTION)
    VALUES (guid, serviceType, name, region, nwpartition, status, servers, description);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc.updateServiceData
--
DROP PROCEDURE IF EXISTS esolutionssvc.updateServiceData$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.updateServiceData(
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
    UPDATE esolutionssvc.services
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
-- Definition of procedure esolutionssvc.removeServiceData
--
DROP PROCEDURE IF EXISTS esolutionssvc.removeServiceData$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.removeServiceData(
    IN guid VARCHAR(128)
)
BEGIN
    UPDATE esolutionssvc.services
    SET STATUS = 'INACTIVE'
    WHERE GUID = guid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc.listServices
--
DROP PROCEDURE IF EXISTS esolutionssvc.listServices$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.listServices(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        SERVICE_TYPE,
        NAME
    FROM esolutionssvc.services
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc.getServiceData
--
DROP PROCEDURE IF EXISTS esolutionssvc.getServiceData$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc.getServiceData(
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
    FROM esolutionssvc.services
    WHERE GUID = guid
    AND STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;

