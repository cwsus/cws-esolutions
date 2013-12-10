--
-- Definition of table `usr_lgn_data`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_data`;
CREATE TABLE `usr_lgn_data` (
    `cn` VARCHAR(128) NOT NULL,
    `userSalt` VARCHAR(128) NOT NULL,
    `saltType` VARCHAR(15) DEFAULT NULL,
    PRIMARY KEY (`userSalt`),
    UNIQUE KEY `UNQ_SaltData` (`userSalt`) USING HASH,
    KEY `IDX_SaltData` (`userSalt`,`saltType`) USING HASH
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

COMMIT;

--
-- Definition of procedure `cwssec`.`addUserSalt`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addUserSalt`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`addUserSalt`(
    IN guid VARCHAR(100),
    IN salt VARCHAR(128),
    in sType VARCHAR(15)
)
BEGIN
    INSERT INTO `cwssec`.`usr_lgn_data` (cn, userSalt, saltType)
    VALUES (guid, salt, sType);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrUserSalt`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrUserSalt`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrUserSalt`(
    IN guid VARCHAR(100),
    IN sType VARCHAR(15)
)
BEGIN
    SELECT userSalt
    FROM `cwssec`.`usr_lgn_data`
    WHERE cn = guid
    AND saltType = sType;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`updateUserSalt`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`updateUserSalt`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`updateUserSalt`(
    IN guid VARCHAR(100),
    IN saltValue VARCHAR(64),
    IN sType VARCHAR(15)
)
BEGIN
    UPDATE `cwssec`.`usr_lgn_data`
    SET userSalt = saltValue
    WHERE cn = guid
    AND saltType = sType;
    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`removeUserData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeUserData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`removeUserData`(
    IN guid VARCHAR(100)
)
BEGIN
    DELETE FROM `cwssec`.`usr_lgn_data`
    WHERE cn = guid;
    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;
