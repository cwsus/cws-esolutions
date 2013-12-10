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
