--
-- Definition of table `cwssec`.`usr_lgn_projects`
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_projects`;
CREATE TABLE `cwssec`.`usr_lgn_projects` (
    `CN` VARCHAR(45) NOT NULL,
    `PROJECT_GUID` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`PROJECT_GUID`),
    CONSTRAINT `FK_PROJECT_GUID`
        FOREIGN KEY (`PROJECT_GUID`)
        REFERENCES `esolutionssvc`.`service_projects` (`PROJECT_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION,
    CONSTRAINT `FK_LGN_GUID`
        FOREIGN KEY (`cn`)
        REFERENCES `cwssec`.`usr_lgn` (`CN`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    FULLTEXT KEY `IDX_LGN_PROJECTS` (`CN`, `PROJECT_GUID`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `cwssec`.`usr_lgn_projects` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `cwssec`.`addProjectToUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`addProjectToUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`addProjectToUser`(
    IN guid VARCHAR(128),
    IN projectId VARCHAR(128)
)
BEGIN
    INSERT INTO `cwssec`.`usr_lgn_projects`
    (CN, PROJECT_GUID)
    VALUES
    (guid, projectId);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`removeProjectFromUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`removeProjectFromUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`removeProjectFromUser`(
    IN guid VARCHAR(128),
    IN projectId VARCHAR(128)
)
BEGIN
    DELETE FROM `cwssec`.`usr_lgn_projects`
    WHERE CN = guid
    AND PROJECT_GUID = projectId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`isUserAuthorizedForProject`
--
DROP PROCEDURE IF EXISTS `cwssec`.`isUserAuthorizedForProject` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`isUserAuthorizedForProject`(
    IN guid VARCHAR(128),
    IN projectId VARCHAR(128)
)
BEGIN
    SELECT COUNT(PROJECT_GUID)
    FROM `cwssec`.`usr_lgn_projects`
    WHERE CN = guid
    AND PROJECT_GUID = projectId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`retrieveAuthorizedProjectsForUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`retrieveAuthorizedProjectsForUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`retrieveAuthorizedProjectsForUser`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        T1.PROJECT_GUID,
        T2.PROJECT_NAME
    FROM `cwssec`.`usr_lgn_projects` T1
    INNER JOIN `esolutionssvc`.`service_projects` T2
    ON T1.PROJECT_GUID = T2.PROJECT_GUID;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
