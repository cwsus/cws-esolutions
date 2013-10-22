/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_sec_roles`
--
DROP TABLE IF EXISTS `cwssec`.`usr_sec_roles`;

COMMIT;

CREATE TABLE `cwssec`.`usr_sec_roles` (
    `usr_sec_role_guid` VARCHAR(100) NOT NULL,
    `usr_sec_role_name` VARCHAR(100) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

COMMIT;

--
-- add in default roles
--

/*!40000 ALTER TABLE `cwssec`.`usr_sec_roles` DISABLE KEYS */;
INSERT INTO `cwssec`.`usr_sec_roles` (usr_sec_role_guid, usr_sec_role_name)
VALUES ('A71ABFD6-41AB-42C8-898F-66B5459CD434', 'UserAdmin');
INSERT INTO `cwssec`.`usr_sec_roles` (usr_sec_role_guid, usr_sec_role_name)
VALUES ('A71ABFD6-41AB-42C8-898F-66B5459CD434', 'Admin');
INSERT INTO `cwssec`.`usr_sec_roles` (usr_sec_role_guid, usr_sec_role_name)
VALUES ('A71ABFD6-41AB-42C8-898F-66B5459CD434', 'User');
/*!40000 ALTER TABLE `cwssec`.`usr_sec_roles` ENABLE KEYS */;

COMMIT;

--
-- Definition of table `cwssec`.`usr_sec_role_mapping`
--
DROP TABLE IF EXISTS `cwssec`.`usr_sec_roles`;
CREATE TABLE `cwssec`.`usr_sec_roles` (
    `usr_sec_role_guid` VARCHAR(100) NOT NULL,
    `usr_sec_lgn_guid` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`usr_sec_lgn_guid`),
    CONSTRAINT `usr_sec_lgn_guid`
		FOREIGN KEY (`usr_sec_role_guid`)
		REFERENCES `cwssec`.`usr_sec_roles`(`usr_sec_role_guid`)
			ON DELETE NO ACTION
			ON UPDATE RESTRICT
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

COMMIT;

--
-- Definition of procedure `cwssec`.`validateUserRoles`
--
DROP PROCEDURE IF EXISTS `cwssec`.`validateUserRoles`;

COMMIT;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `validateUserRoles`(
    IN userGuid VARCHAR(128),
	IN roleGuid VARCHAR(128)
)
BEGIN
	SELECT COUNT(*)
	FROM `cwssec`.`usr_sec_roles`
	WHERE usr_sec_lgn_guid = userGuid
	AND usr_sec_role_guid = roleGuid;
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