-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version    5.0.51b-community-nt-log
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `usr_lgn_data`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn_data`;
CREATE TABLE `usr_lgn_data` (
  `cn` VARCHAR(128) NOT NULL,
  `userSalt` VARCHAR(128) NOT NULL,
  `saltType` VARCHAR(15) DEFAULT NULL,
  `secretKey` BLOB NOT NULL,
  `vector` BLOB NOT NULL,
  PRIMARY KEY (`userSalt`),
  UNIQUE KEY `UNQ_SaltData` (`userSalt`) USING HASH,
  KEY `IDX_SaltData` (`userSalt`,`saltType`) USING HASH
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

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

--
-- Dumping data for table `usr_reset_data`
--
DROP TABLE IF EXISTS `cwssec`.`usr_reset_data`;
CREATE TABLE `cwssec`.`usr_reset_data` (
	`cn` VARCHAR(128) NOT NULL,
	`resetKey` VARCHAR(128) NOT NULL,
	`createTime` BIGINT NOT NULL,
	`smsCode` VARCHAR(8),
	PRIMARY KEY (`cn`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `usr_reset_data`
--
/*!40000 ALTER TABLE `usr_reset_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `usr_reset_data` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`insertResetData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`insertResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`insertResetData`(
	IN guid VARCHAR(128),
	IN resetId VARCHAR(128),
    IN timeCreated BIGINT(20),
	IN smsId VARCHAR(8)
)
BEGIN
	INSERT INTO `cwssec`.`usr_reset_data`
	(cn, resetKey, createTime, smsCode)
	VALUES
	(guid, resetId, timeCreated, smsCode);

	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`getResetData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`getResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`getResetData`(
	IN resetId VARCHAR(128)
)
BEGIN
	SELECT cn, createTime
	FROM usr_reset_data
	WHERE resetKey = resetId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`verifySmsCodeForReset`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`verifySmsCodeForReset`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`verifySmsCodeForReset`(
	IN guid VARCHAR(128),
	IN resetId VARCHAR(128),
	IN smsId VARCHAR(8)
)
BEGIN
	SELECT resetKey, createTime
	FROM usr_reset_data
	WHERE cn = guid
	AND resetKey = resetId
	AND smsCode = smsId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`listActiveResetRequests`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`listActiveResetRequests`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`listActiveResetRequests`(
)
BEGIN
	SELECT cn, resetKey, createTime
	FROM usr_reset_data;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of event `cwssec`.`expireResetData`
--
DROP EVENT IF EXISTS `cwssec`.`expireResetData`;
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */;
CREATE DEFINER=`appuser`@`localhost` EVENT `cwssec`.`expireResetData`
ON SCHEDULE EVERY 15 MINUTE
DO
	DELETE FROM `cwssec`.`usr_reset_data`
	WHERE createTime >= CURRENT_TIMESTAMP;

	COMMIT;
COMMIT;

--
-- Definition of procedure `cwssec`.`removeResetData`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`removeResetData`(
    IN commonName VARCHAR(128),
	IN resetId VARCHAR(128)
)
BEGIN
    DELETE FROM usr_reset_data
	WHERE resetKey = resetId
    AND cn = commonName;

    COMMIT;
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
