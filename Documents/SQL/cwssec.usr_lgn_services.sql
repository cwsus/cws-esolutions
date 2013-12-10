--
-- Definition of table `cwssec`.`usr_lgn_services`
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_services`;
CREATE  TABLE `cwssec`.`usr_lgn_services` (
    `usr_svc_svcid` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_svc_svcname` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_svc_uri` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_svc_description` TEXT CHARACTER SET UTF8 NOT NULL,
    PRIMARY KEY (`usr_svc_svcid`),
    INDEX `PK_usr_svc_svcid` (`usr_svc_svcid` ASC)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

--
-- add in default services
--
/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` DISABLE KEYS */;
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('96E4E53E-FE87-446C-AF03-0F5BC6527B9D', 'AppMgmt', 'application-management', 'Service ID for Application Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('B52B1DE9-37A4-4554-B85E-2EA28C4EE3DD', 'DNSMgmt', 'dns-service', 'Service ID for DNS Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('F7D1DAB8-DADB-4E7B-8596-89D1BE230E75', 'FileMgmt', 'application-management', 'Service ID for File Handling');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('4B081972-92C3-455B-9403-B81E68C538B6', 'kbase', 'knowledgebase', 'Service ID for KnowledgeBase');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('5C0B0A54-2456-45C9-A435-B485ED36FAC7', 'Messaging', 'service-messaging', 'Service ID for Messaging');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('7CE2B9E8-9FCF-4096-9CAE-10961F50FA81', 'Search', 'search', 'Service ID for Search');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('45F6BC9E-F45C-4E2E-B5BF-04F93C8F512E', 'SysMgmt', 'system-management', 'Service ID for System Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('D1B5D088-32B3-4AA1-9FCF-822CB476B649', 'PlatformMgmt', 'service-management', 'Service ID for Platform Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('A0F3C71F-5FAF-45B4-AA34-9779F64D397E', 'ProjectMgmt', 'service-management', 'Service ID for Project Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('3F0D3FB5-56C9-4A90-B177-4E1593088DBF', 'SystemCheck', 'system-check', 'Service ID for System Checks');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('AEB46994-57B4-4E92-90AA-A4046F60B830', 'UserMgmt', 'user-management', 'Service ID for User Management');
INSERT INTO `cwssec`.`usr_lgn_services`
VALUES ('0C1C5F83-3EDD-4635-9F1E-6A9B5383747E', 'DatacenterMgmt', 'service-management', 'Service ID for Datacenter Management');

/*!40000 ALTER TABLE `cwssec`.`usr_lgn_services` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrAvailableServices`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrAvailableServices` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrAvailableServices`(
)
BEGIN
    SELECT usr_svc_uri, usr_svc_svcid
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
