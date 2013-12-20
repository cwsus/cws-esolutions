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
COMMIT;

ALTER TABLE `cwssec`.`usr_reset_data` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `cwssec`.`insertResetData`
--
DROP PROCEDURE IF EXISTS `cwssec`.`insertResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`insertResetData`(
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
COMMIT$$

--
-- Definition of procedure `cwssec`.`getResetData`
--
DROP PROCEDURE IF EXISTS `cwssec`.`getResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`getResetData`(
    IN resetId VARCHAR(128)
)
BEGIN
    SELECT cn, createTime
    FROM usr_reset_data
    WHERE resetKey = resetId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`verifySmsCodeForReset`
--
DROP PROCEDURE IF EXISTS `cwssec`.`verifySmsCodeForReset`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`verifySmsCodeForReset`(
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
COMMIT$$

--
-- Definition of procedure `cwssec`.`listActiveResetRequests`
--
DROP PROCEDURE IF EXISTS `cwssec`.`listActiveResetRequests`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`listActiveResetRequests`(
)
BEGIN
    SELECT cn, resetKey, createTime
    FROM usr_reset_data;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `cwssec`.`removeResetData`
--
DROP PROCEDURE IF EXISTS `cwssec`.`removeResetData`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`removeResetData`(
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
COMMIT$$

DELIMITER ;
COMMIT;
