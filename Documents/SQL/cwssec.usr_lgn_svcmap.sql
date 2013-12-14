--
-- Definition of table `cwssec`.`usr_lgn_svcmap`
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_svcmap`;
CREATE TABLE `cwssec`.`usr_lgn_svcmap` (
    `CN` VARCHAR(128) NOT NULL,
    `USR_SVC_SVCID` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`USR_SVC_SVCID`),
    CONSTRAINT `FK_CN`
        FOREIGN KEY (`CN`)
        REFERENCES `cwssec`.`usr_lgn` (`CN`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `FK_SVC_ID`
        FOREIGN KEY (`USR_SVC_SVCID`)
        REFERENCES `cwssec`.`usr_lgn_services` (`USR_SVC_SVCID`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    FULLTEXT KEY `IDX_SERVICE_MAP` (`CN`, `USR_SVC_SVCID`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `cwssec`.`usr_lgn_svcmap` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `cwssec`.`addServiceToUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`addServiceToUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`addServiceToUser`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    INSERT INTO `cwssec`.`usr_lgn_svcmap`
    (CN, USR_SVC_SVCID)
    VALUES
    (guid, serviceId);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`removeServiceFromUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`removeServiceFromUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`removeServiceFromUser`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    DELETE FROM `cwssec`.`usr_lgn_svcmap`
    WHERE CN = guid
    AND USR_SVC_SVCID = serviceId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`isUserAuthorizedForService`
--
DROP PROCEDURE IF EXISTS `cwssec`.`isUserAuthorizedForService` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`isUserAuthorizedForService`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    SELECT COUNT(USR_SVC_SVCID)
    FROM `cwssec`.`usr_lgn_svcmap`
    WHERE CN = guid
    AND USR_SVC_SVCID = serviceId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`retrAuthorizedServices`
--
DROP PROCEDURE IF EXISTS `cwssec`.`retrAuthorizedServices` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`retrAuthorizedServices`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        T1.USR_SVC_SVCID,
        T1.USR_SVC_SVCNAME
    FROM USR_LGN_SERVICES T1
    INNER JOIN USR_LGN_SVCMAP T2
    ON T1.USR_SVC_SVCID = T2.USR_SVC_SVCID;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`listServicesForUser`
--
DROP PROCEDURE IF EXISTS `cwssec`.`listServicesForUser` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`listServicesForUser`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT usr_svc_svcid
    FROM usr_lgn_svcmap
    WHERE usr_lgn_guid = userGuid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
