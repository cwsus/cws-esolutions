/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_lgn_services`
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_services`;
CREATE  TABLE `cwssec`.`usr_lgn_services` (
    `usr_svc_svcid` VARCHAR(128) NOT NULL,
    `usr_svc_svcname` VARCHAR(128) NOT NULL,
    `usr_svc_description` TEXT NOT NULL,
	PRIMARY KEY (`usr_svc_svcid`),
	INDEX `PK_usr_svc_svcid` (`usr_svc_svcid` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- add in default services
--
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` DISABLE KEYS */;

INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('96E4E53E-FE87-446C-AF03-0F5BC6527B9D', 'AppMgmt', 'Service ID for Application Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD', 'DNSMgmt', 'Service ID for DNS Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('F7D1DAB8-DADB-4E7B-8596-89D1BE230E75', 'FileMgmt', 'Service ID for File Handling');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('4B081972-92C3-455B-9403-B81E68C538B6', 'kbase', 'Service ID for KnowledgeBase');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('5C0B0A54-2456-45C9-A435-B485ED36FAC7', 'Messaging', 'Service ID for Messaging');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('7CE2B9E8-9FCF-4096-9CAE-10961F50FA81', 'Search', 'Service ID for Search');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E', 'SysMgmt', 'Service ID for System Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('D1B5D088-32B3-4AA1-9FCF-822CB476B649', 'PlatformMgmt', 'Service ID for Platform Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('A0F3C71F-5FAF-45B4-AA34-9779F64D397E', 'ProjectMgmt', 'Service ID for Project Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('3F0D3FB5-56C9-4A90-B177-4E1593088DBF', 'SystemCheck', 'Service ID for System Checks');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('AEB46994-57B4-4E92-90AA-A4046F60B830', 'UserMgmt', 'Service ID for User Management');
INSERT INTO `cwssec`.`usr_lgn_services` (usr_svc_svcid, usr_svc_svcname, usr_svc_description)
VALUES ('0C1C5F83-3EDD-4635-9F1E-6A9B5383747E', 'DatacenterMgmt', 'Service ID for Datacenter Management');

/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrieve_svc_list`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrieve_svc_list` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrieve_svc_list`(
)
BEGIN
    SELECT *
	FROM `cwssec`.`usr_lgn_services`
	ORDER BY usr_svc_svcid ASC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of table `cwssec`.`usr_lgn_svcmap`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_svcmap`;
CREATE  TABLE `cwssec`.`usr_lgn_svcmap` (
    `usr_lgn_guid` VARCHAR(128) NOT NULL ,
    `usr_svc_svcid` VARCHAR(128) NOT NULL,
	PRIMARY KEY (`usr_svc_svcid`),
	INDEX `PK_usr_svc_svcid` (`usr_svc_svcid` ASC),
	CONSTRAINT `FK_usr_svc_svcid`
		FOREIGN KEY (`usr_svc_svcid`)
		REFERENCES `cwssec`.`usr_lgn_services` (`usr_svc_svcid`)
			ON DELETE RESTRICT
			ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `cwssec`.`usr_lgn_svcmap`
--
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_svcmap` DISABLE KEYS */;
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_svcmap` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `cwssec`.`addServiceToUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addServiceToUser`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`addServiceToUser`(
    IN userSecId VARCHAR(45),
    IN serviceid VARCHAR(45)
)
BEGIN
	INSERT INTO `cwssec`.`usr_lgn_svcmap` (usr_lgn_guid, usr_svc_svcid)
	VALUES (userSecId, serviceId);
	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`removeServiceFromUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeServiceFromUser`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`removeServiceFromUser`(
    IN userSecId VARCHAR(45),
    IN serviceid VARCHAR(45)
)
BEGIN
	DELETE FROM `cwssec`.`usr_lgn_svcmap`
    WHERE usr_lgn_guid = userSecId
    AND usr_svc_svcid = serviceid;
	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`verifySvcForUser`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`verifySvcForUser`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`verifySvcForUser`(
    IN userSecId VARCHAR(45),
    IN serviceid VARCHAR(45)
)
BEGIN
    SELECT COUNT(*)
    FROM `cwssec`.`usr_lgn_svcmap`
    WHERE usr_lgn_guid = userSecId
    AND usr_svc_svcid = serviceid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`listServicesForUser`
-- This will return the service UID's and descriptions for the provided user
-- it will NOT provide back any user information aside from that
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`listServicesForUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`listServicesForUser`(
	IN userGuid VARCHAR(128)
)
BEGIN
	SELECT usr_svc_svcid
	FROM usr_lgn_svcmap
	WHERE usr_lgn_guid = userGuid;
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