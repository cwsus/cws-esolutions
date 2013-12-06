/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_lgn_projects`
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_projects`;
CREATE  TABLE `cwssec`.`usr_lgn_projects` (
    `CN` VARCHAR(45) NOT NULL,
    `PROJECT_GUID` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`PROJECT_GUID`),
    CONSTRAINT `FK_PROJECT_GUID`
        FOREIGN KEY (`PROJECT_GUID`)
        REFERENCES `esolutionssvc`.`installed_platforms` (`PROJECT_GUID`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_LGN_PROJECTS` (`CN`, `PROJECT_GUID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `cwssec`.`usr_lgn_services`
--
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`addProjectToUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addProjectToUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`addProjectToUser`(
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

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`removeProjectFromUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeProjectFromUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`removeProjectFromUser`(
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

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`isUserAuthorizedForProject`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`isUserAuthorizedForProject` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`isUserAuthorizedForProject`(
    IN guid VARCHAR(128),
    IN projectId VARCHAR(128)
)
BEGIN
    SELECT COUNT(PROJECT_ID)
    FROM `cwssec`.`usr_lgn_projects`
    WHERE CN = guid
    AND PROJECT_GUID = projectId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrieveAuthorizedProjectsForUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrieveAuthorizedProjectsForUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrieveAuthorizedProjectsForUser`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT PROJECT_GUID
    FROM `cwssec`.`usr_lgn_projects`
    WHERE CN = guid
    ORDER BY PROJECT_GUID ASC;
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
