/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`messaging`
--
DROP TABLE IF EXISTS `esolutionssvc`.`messaging`;
CREATE TABLE `esolutionssvc`.`messaging` (
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
-- Dumping data for table `esolutionssvc`.`messaging`
--
/*!40000 ALTER TABLE `esolutionssvc`.`messaging` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`messaging` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`submit_message`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`submit_message`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`submit_message`(
    IN requestorId VARCHAR(45),
    IN requestorEmail VARCHAR(45),
    IN submissionDate BIGINT,
    IN messageId VARCHAR(45),
    IN messageBody TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`messaging`
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
-- Definition of procedure `esolutionssvc`.`submit_message`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`submit_svc_message`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`submit_svc_message`(
    IN requestorId VARCHAR(45),
    IN requestorEmail VARCHAR(45),
    IN submissionDate BIGINT,
    IN messageId VARCHAR(45),
    IN messageSubject VARCHAR(100),
    IN messageBody TEXT,
    IN isActive BOOLEAN
)
BEGIN
    INSERT INTO `esolutionssvc`.`messaging`
    (
        svc_message_submittor_uid, svc_message_submittor_email, svc_message_datesubmitted, svc_message_messageid, svc_message_subject, svc_message_body, svc_message_type
    )
    VALUES
    (
        requestorId, requestorEmail, submissionDate, messageId, messageSubject, messageBody, 'service'
    );
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`update_svc_message`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`update_svc_message`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`update_svc_message`(
    IN requestorId VARCHAR(45),
    IN submissionDate BIGINT,
    IN messageSubject VARCHAR(100),
    IN messageBody TEXT,
    IN isActive BOOLEAN,
    IN messageId VARCHAR(45)
)
BEGIN
    UPDATE `esolutionssvc`.`messaging`
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
-- Definition of procedure `esolutionssvc`.`retrieve_service_messages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrieve_service_messages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrieve_service_messages`(
)
BEGIN
    SELECT
        svc_message_subject,
        svc_message_body,
        svc_message_submittor_uid,
        svc_message_submittor_email,
        svc_message_datesubmitted,
        svc_message_messageid
    FROM `esolutionssvc`.`messaging`
    WHERE svc_message_type = 'service'
    AND svc_message_active = true
    ORDER BY svc_message_datesubmitted DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrieve_service_messages`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrieve_service_message`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrieve_service_message`(
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
    FROM `esolutionssvc`.`messaging`
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