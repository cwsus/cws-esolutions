/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`service_projects`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_projects`;
CREATE TABLE `esolutionssvc`.`service_projects` (
    `PROJECT_GUID` VARCHAR(128) NOT NULL,
    `PROJECT_CODE` VARCHAR(128) NOT NULL,
    `PROJECT_STATUS` VARCHAR(45) NOT NULL DEFAULT 'INACTIVE',
    `PRIMARY_OWNER` VARCHAR(128) NOT NULL,
    `SECONDARY_OWNER` VARCHAR(128),
    `CONTACT_EMAIL` VARCHAR(128) NOT NULL,
    `INCIDENT_QUEUE` VARCHAR(128) NOT NULL,
    `CHANGE_QUEUE` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`PROJECT_GUID`),
    FULLTEXT KEY `IDX_PROJECTS` (`PROJECT_CODE`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `CONTACT_EMAIL`)
) ENGINE=MYISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `esolutionssvc`.`service_projects`
--
/*!40000 ALTER TABLE `esolutionssvc`.`service_projects` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`service_projects` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getProjectByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getProjectByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getProjectByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_CODE, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, CONTACT_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE,
    MATCH (`PROJECT_CODE`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `CONTACT_EMAIL`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_projects`
    WHERE MATCH (`PROJECT_CODE`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `CONTACT_EMAIL`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`insertNewProject`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertNewProject`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`insertNewProject`(
    IN projectGuid VARCHAR(128),
    IN projectCode VARCHAR(45),
    IN primaryOwner VARCHAR(45),
    IN secondaryOwner VARCHAR(45),
    IN contactEmail VARCHAR(50),
    IN incidentQueue VARCHAR(100),
    IN changeQueue VARCHAR(50),
    IN status VARCHAR(45)
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_projects`
    (PROJECT_GUID, PROJECT_CODE, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, CONTACT_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE)
    VALUES
    (projectGuid, projectCode, status, primaryOwner, secondaryOwner, contactEmail, incidentQueue, changeQueue);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`updateProjectData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updateProjectData`(
    IN projectGuid VARCHAR(128),
    IN primaryOwner VARCHAR(45),
    IN secondaryOwner VARCHAR(45),
    IN contactEmail VARCHAR(50),
    IN incidentQueue VARCHAR(100),
    IN changeQueue VARCHAR(50)
)
BEGIN
    UPDATE `esolutionssvc`.`service_projects`
    SET
        PRIMARY_OWNER = primaryOwner,
        SECONDARY_OWNER = secondaryOwner,
        CONTACT_EMAIL = contactEmail,
        INCIDENT_QUEUE = incidentQueue,
        CHANGE_QUEUE = changeQueue
    WHERE PROJECT_GUID = projectGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`removeProjectData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removeProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`removeProjectData`(
    IN projectGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_projects`
    SET PROJECT_STATUS = 'INACTIVE'
    WHERE PROJECT_GUID = projectGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`getProjectData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getProjectData`(
    IN projectGuid VARCHAR(128)
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_CODE, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, CONTACT_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE
    FROM `esolutionssvc`.`service_projects`
    WHERE PROJECT_GUID = projectGuid
    AND PROJECT_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `esolutionssvc`.`listProjects`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listProjects`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`listProjects`(
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_CODE, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, CONTACT_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE
    FROM `esolutionssvc`.`service_projects`
    WHERE PROJECT_STATUS = 'ACTIVE';
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