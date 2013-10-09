﻿/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`service_platforms`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_platforms`;
CREATE TABLE `esolutionssvc`.`service_platforms` (
    `PLATFORM_GUID` VARCHAR(128) NOT NULL,
    `PLATFORM_NAME` VARCHAR(128) NOT NULL,
    `PLATFORM_REGION` VARCHAR(15) NOT NULL,
    `PLATFORM_DMGR` VARCHAR(128) NOT NULL,
    `PLATFORM_APPSERVERS` TEXT NOT NULL,
    `PLATFORM_WEBSERVERS` TEXT NOT NULL,
    `PLATFORM_STATUS` VARCHAR(50) NOT NULL DEFAULT 'INACTIVE',
    `PLATFORM_DESC` TEXT,
    PRIMARY KEY (`PLATFORM_GUID`),
    CONSTRAINT `UQ_PLATFORMS` UNIQUE (`PLATFORM_DMGR`),
    FULLTEXT KEY `IDX_PLATFORMS` (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`),
	CONSTRAINT `FK_DMGR_GUID`
		FOREIGN KEY (`PLATFORM_DMGR`)
		REFERENCES `esolutionssvc`.`installed_systems` (`SYSTEM_GUID`)
			ON DELETE RESTRICT
			ON UPDATE NO ACTION
) ENGINE=MYISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `esolutionssvc`.`service_platforms`
--
/*!40000 ALTER TABLE `esolutionssvc`.`service_platforms` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`service_platforms` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getPlatformByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPlatformByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getPlatformByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT PLATFORM_GUID, PLATFORM_NAME, PLATFORM_REGION, PLATFORM_DMGR, PLATFORM_APPSERVERS, PLATFORM_WEBSERVERS, PLATFORM_DESC,
    MATCH (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_platforms`
    WHERE MATCH (`PLATFORM_NAME`, `PLATFORM_REGION`, `PLATFORM_DMGR`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`addNewPlatform`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`addNewPlatform`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`addNewPlatform`(
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

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`updatePlatformData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updatePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updatePlatformData`(
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

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`removePlatformData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removePlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`removePlatformData`(
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_platforms`
    SET PLATFORM_STATUS = 'INACTIVE'
    WHERE PLATFORM_GUID = platformGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`getPlatformData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPlatformData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getPlatformData`(
    IN platformGuid VARCHAR(128)
)
BEGIN
    SELECT PLATFORM_GUID, PLATFORM_NAME, PLATFORM_REGION, PLATFORM_DMGR, PLATFORM_APPSERVERS, PLATFORM_WEBSERVERS, PLATFORM_DESC
    FROM `esolutionssvc`.`service_platforms`
    WHERE PLATFORM_GUID = platformGuid
    AND PLATFORM_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`listPlatforms`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listPlatforms`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`listPlatforms`(
)
BEGIN
    SELECT PLATFORM_GUID, PLATFORM_NAME, PLATFORM_REGION, PLATFORM_DMGR, PLATFORM_APPSERVERS, PLATFORM_WEBSERVERS, PLATFORM_DESC
    FROM `esolutionssvc`.`service_platforms`
    WHERE PLATFORM_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;