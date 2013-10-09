/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cws;

--
-- Definition of table `cws`.`messaging`
--
DROP TABLE IF EXISTS `cws`.`messaging`;
CREATE TABLE `cws`.`messaging` (
    `svc_message_submittor_uid` VARCHAR(45),
    `svc_message_modifier` VARCHAR(45),
    `svc_message_submittor_email` VARCHAR(45),
    `svc_message_datesubmitted` BIGINT NOT NULL,
    `svc_message_messageid` VARCHAR(45) NOT NULL,
    `svc_message_body` TEXT,
    `svc_message_subject` VARCHAR(100),
    `svc_message_type` VARCHAR(45) NOT NULL,
    `svc_message_active` BOOLEAN DEFAULT FALSE,
    PRIMARY KEY  (`svc_message_messageid`),
    FULLTEXT KEY `messaging_search` (`svc_message_subject`, `svc_message_body`, `svc_message_submittor_uid`, `svc_message_submittor_email`, `svc_message_messageid`, `svc_message_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cws`.`messaging`
--
/*!40000 ALTER TABLE `cws`.`messaging` DISABLE KEYS */;
/*!40000 ALTER TABLE `cws`.`messaging` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cws`.`submitSvcMessage`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`submitSvcMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`submitSvcMessage`(
    IN requestorId VARCHAR(45),
    IN requestorEmail VARCHAR(45),
    IN submissionDate BIGINT,
    IN messageId VARCHAR(45),
    IN messageBody TEXT
)
BEGIN
    INSERT INTO `cws`.`messaging`
    (
        svc_message_submittor_uid, svc_message_submittor_email, svc_message_datesubmitted, svc_message_messageid, svc_message_body, svc_message_type
    )
    VALUES
    (
        requestorId, requestorEmail, submissionDate, messageId, messageBody, 'contact'
    );
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`updateSvcMessage`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`updateSvcMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`updateSvcMessage`(
    IN requestorId VARCHAR(45),
    IN submissionDate BIGINT,
    IN messageSubject VARCHAR(100),
    IN messageBody TEXT,
    IN isActive BOOLEAN,
    IN messageId VARCHAR(45)
)
BEGIN
    UPDATE `cws`.`messaging`
    SET
        svc_message_modifier = requestorId,
        svc_message_datesubmitted = submissionDate,
        svc_message_subject = messageSubject,
        svc_message_body = messageBody,
        svc_message_active = isActive
    WHERE svc_message_messageid = messageId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`retrieveSvcMessages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`retrieveSvcMessages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`retrieveSvcMessages`(
    IN messageId VARCHAR(45)
)
BEGIN
    SELECT
        svc_message_subject,
        svc_message_body,
        svc_message_submittor_uid,
        svc_message_submittor_email,
        svc_message_datesubmitted,
        svc_message_messageid
    FROM `cws`.`messaging`
    WHERE svc_message_messageid = messageId;
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