--
-- Definition of table `cwssec`.`usr_key_data`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_key_data`;
COMMIT;

CREATE  TABLE `cwssec`.`usr_key_data` (
    `usr_lgn_guid` VARCHAR(45) NOT NULL,
    `usr_key_priv` VARBINARY(4352) NULL,
    PRIMARY KEY (`usr_lgn_guid`),
    INDEX `usr_ind` (`usr_lgn_guid` ASC)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

COMMIT;

--
-- Definition of procedure `cwssec`.`addUserKeys`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addUserKeys` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`addUserKeys`(
    IN userGuid VARCHAR(45),
    IN privKey VARBINARY(4352)
)
BEGIN
    INSERT INTO usr_key_data
    (usr_lgn_guid, usr_key_priv)
    VALUES
    (userGuid, privKey);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrUserKeys`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrUserKeys` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrUserKeys`(
    IN userGuid VARCHAR(45)
)
BEGIN
    SELECT usr_key_priv
    FROM usr_key_data
    WHERE usr_lgn_guid = userGuid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrUserKeys`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`deleteUserKeys` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`deleteUserKeys`(
    IN userGuid VARCHAR(45)
)
BEGIN
    DELETE FROM `cwssec`.`usr_key_data`
    WHERE usr_lgn_guid = userGuid;

    UPDATE `cwssec`.`usr_lgn`
    SET CWSPUBLICKEY = NULL
    WHERE CN = userGuid;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;
