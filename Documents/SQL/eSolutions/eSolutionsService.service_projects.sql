--
-- Definition of table `esolutionssvc`.`service_projects`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_projects`;
CREATE TABLE `esolutionssvc`.`service_projects` (
    `PROJECT_GUID` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `PROJECT_NAME` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `PROJECT_STATUS` VARCHAR(45) CHARACTER SET UTF8 NOT NULL DEFAULT 'INACTIVE',
    `PRIMARY_OWNER` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `SECONDARY_OWNER` VARCHAR(128) CHARACTER SET UTF8,
    `DEV_EMAIL` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `PROD_EMAIL` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `INCIDENT_QUEUE` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `CHANGE_QUEUE` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    PRIMARY KEY (`PROJECT_GUID`),
    FULLTEXT KEY `IDX_PROJECTS` (`PROJECT_NAME`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `DEV_EMAIL`, `PROD_EMAIL`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`service_projects` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`getProjectByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getProjectByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getProjectByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_NAME, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, DEV_EMAIL, PROD_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE,
    MATCH (`PROJECT_NAME`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `DEV_EMAIL`, `PROD_EMAIL`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_projects`
    WHERE MATCH (`PROJECT_NAME`, `PROJECT_STATUS`, `PRIMARY_OWNER`, `SECONDARY_OWNER`, `DEV_EMAIL`, `PROD_EMAIL`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`insertNewProject`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`insertNewProject`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`insertNewProject`(
    IN projectGuid VARCHAR(128),
    IN projectName VARCHAR(45),
    IN primaryOwner VARCHAR(45),
    IN secondaryOwner VARCHAR(45),
    IN devEmail VARCHAR(128),
    IN prodEmail VARCHAR(128),
    IN incidentQueue VARCHAR(100),
    IN changeQueue VARCHAR(50),
    IN status VARCHAR(45)
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_projects`
    (PROJECT_GUID, PROJECT_NAME, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, DEV_EMAIL, PROD_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE)
    VALUES
    (projectGuid, projectName, status, primaryOwner, secondaryOwner, devEmail, prodEmail, incidentQueue, changeQueue);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`updateProjectData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`updateProjectData`(
    IN projectGuid VARCHAR(128),
    IN primaryOwner VARCHAR(45),
    IN secondaryOwner VARCHAR(45),
    IN devEmail VARCHAR(128),
    IN prodEmail VARCHAR(128),
    IN incidentQueue VARCHAR(100),
    IN changeQueue VARCHAR(50)
)
BEGIN
    UPDATE `esolutionssvc`.`service_projects`
    SET
        PRIMARY_OWNER = primaryOwner,
        SECONDARY_OWNER = secondaryOwner,
        DEV_EMAIL = devEmail,
        PROD_EMAIL = prodEmail,
        INCIDENT_QUEUE = incidentQueue,
        CHANGE_QUEUE = changeQueue
    WHERE PROJECT_GUID = projectGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`removeProjectData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`removeProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`removeProjectData`(
    IN projectGuid VARCHAR(128)
)
BEGIN
    UPDATE `esolutionssvc`.`service_projects`
    SET PROJECT_STATUS = 'INACTIVE'
    WHERE PROJECT_GUID = projectGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`getProjectData`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getProjectData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getProjectData`(
    IN projectGuid VARCHAR(128)
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_NAME, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, DEV_EMAIL, PROD_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE
    FROM `esolutionssvc`.`service_projects`
    WHERE PROJECT_GUID = projectGuid
    AND PROJECT_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getProjectCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getProjectCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getProjectCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`service_projects`
    WHERE PROJECT_STATUS = 'ACTIVE';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`listProjects`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`listProjects`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`listProjects`(
    IN startRow INT
)
BEGIN
    SELECT PROJECT_GUID, PROJECT_NAME, PROJECT_STATUS, PRIMARY_OWNER, SECONDARY_OWNER, DEV_EMAIL, PROD_EMAIL, INCIDENT_QUEUE, CHANGE_QUEUE
    FROM `esolutionssvc`.`service_projects`
    WHERE PROJECT_STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
