--
-- Definition of table `esolutionssvc`.`service_messages`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_messages`;
CREATE TABLE `esolutionssvc`.`service_messages` (
    `svc_message_id` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `svc_message_title` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    `svc_message_txt` TEXT CHARACTER SET UTF8 NOT NULL,
    `svc_message_author` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `svc_message_submitdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `svc_message_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `svc_message_alert` BOOLEAN NOT NULL DEFAULT FALSE,
    `svc_message_expires` BOOLEAN NOT NULL DEFAULT FALSE,
    `svc_message_expirydate` TIMESTAMP,
    `svc_message_modifiedon` TIMESTAMP,
    `svc_message_modifiedby` VARCHAR(45) CHARACTER SET UTF8,
    PRIMARY KEY (`svc_message_id`, `svc_message_title`), -- prevent the same message from being submitted twice (we hope)
    FULLTEXT KEY `FTK_svcMessages` (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

-- Dumping data for table `esolutionssvc`.`service_messages`
--
/*!40000 ALTER TABLE `esolutionssvc`.`service_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`service_messages` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getMessagesByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getMessagesByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getMessagesByAttribute`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_submitdate,
        svc_message_active,
        svc_message_alert,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby,
    MATCH (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_messages`
    WHERE MATCH (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND svc_message_active = TRUE
    AND (svc_message_expirydate > NOW() OR svc_message_expirydate = '0000-00-00 00:00:00' OR svc_message_expires = FALSE)
    ORDER BY svc_message_id DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`submitSvcMessage`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`submitSvcMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`submitSvcMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN messageAuthor VARCHAR(45),
    IN active BOOLEAN,
    IN isAlert BOOLEAN,
    IN expiry BOOLEAN,
    IN expiryDate BIGINT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_messages`
    (
        svc_message_id, svc_message_title, svc_message_txt, svc_message_author, svc_message_submitdate, svc_message_active, svc_message_alert, svc_message_expires, svc_message_expirydate
    )
    VALUES
    (
        messageId, messageTitle, messageText, messageAuthor, NOW(), active, isAlert, expiry, expiryDate
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`updateServiceMessage`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateServiceMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`updateServiceMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN active BOOLEAN,
    IN isAlert BOOLEAN,
    IN expiry BOOLEAN,
    IN expiryDate BIGINT,
    IN modifyAuthor VARCHAR(45)
)
BEGIN
    UPDATE `esolutionssvc`.`service_messages`
    SET
        svc_message_title = messageTitle,
        svc_message_txt = messageText,
        svc_message_active = active,
        svc_message_alert = isAlert,
        svc_message_expires = expiry,
        svc_message_expirydate = expiryDate,
        svc_message_modifiedon = NOW(),
        svc_message_modifiedby = modifyAuthor
    WHERE svc_message_id = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrSvcMessages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrServiceMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`retrServiceMessage`(
    IN requestId VARCHAR(45)
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_submitdate,
        svc_message_active,
        svc_message_alert,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby
    FROM `esolutionssvc`.`service_messages`
    WHERE svc_message_id = requestId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrAllSvcMessages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrAllSvcMessages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`retrAllSvcMessages`(
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_submitdate,
        svc_message_active,
        svc_message_alert,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby
    FROM `esolutionssvc`.`service_messages`
    WHERE svc_message_active = TRUE
    AND svc_message_alert = FALSE
    AND (svc_message_expirydate > NOW() OR svc_message_expirydate = '0000-00-00 00:00:00' OR svc_message_expires = FALSE)
    ORDER BY svc_message_id DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrAlertMessages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrAlertMessages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`retrAlertMessages`(
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_submitdate,
        svc_message_active,
        svc_message_alert,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby
    FROM `esolutionssvc`.`service_messages`
    WHERE svc_message_active = TRUE
    AND svc_message_alert = TRUE
    AND (svc_message_expirydate > NOW() OR svc_message_expirydate = '0000-00-00 00:00:00' OR svc_message_expires = FALSE)
    ORDER BY svc_message_id DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;
