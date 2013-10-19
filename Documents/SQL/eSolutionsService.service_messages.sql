/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`service_messages`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_messages`;
CREATE TABLE `esolutionssvc`.`service_messages` (
    `svc_message_id` VARCHAR(128) NOT NULL,
    `svc_message_title` VARCHAR(100) NOT NULL,
    `svc_message_txt` TEXT NOT NULL,
    `svc_message_author` VARCHAR(45) NOT NULL,
    `svc_message_email` VARCHAR(45) NOT NULL,
    `svc_message_submitdate` BIGINT NOT NULL,
    `svc_message_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `svc_message_expires` BOOLEAN NOT NULL DEFAULT FALSE,
    `svc_message_expirydate` BIGINT,
    `svc_message_modifiedon` BIGINT,
    `svc_message_modifiedby` VARCHAR(45),
    PRIMARY KEY (`svc_message_id`, `svc_message_title`), -- prevent the same message from being submitted twice (we hope)
    FULLTEXT KEY `FTK_svcMessages` (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getMessagesByAttribute`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_email,
        svc_message_submitdate,
        svc_message_active,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby,
    MATCH (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`service_messages`
    WHERE MATCH (`svc_message_id`, `svc_message_title`, `svc_message_txt`, `svc_message_author`)
    AGAINST (+searchTerms IN BOOLEAN MODE);
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
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`submitSvcMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN messageAuthor VARCHAR(45),
    IN authorEmail VARCHAR(45),
    IN active BOOLEAN,
    IN expiry BOOLEAN,
    IN expiryDate BIGINT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_messages`
    (
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_email,
        svc_message_submitdate,
        svc_message_active,
        svc_message_expires,
        svc_message_expirydate
    )
    VALUES
    (
        messageId,
        messageTitle,
        messageText,
        messageAuthor,
        authorEmail,
        UNIX_TIMESTAMP(NOW()),
        active,
        expiry,
        expiryDate
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
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updateServiceMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN active BOOLEAN,
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
        svc_message_expires = expiry,
        svc_message_expirydate = expiryDate,
        svc_message_modifiedon = UNIX_TIMESTAMP(),
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
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrServiceMessage`(
    IN requestId VARCHAR(45)
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_email,
        svc_message_submitdate,
        svc_message_active,
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
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrServiceMessages`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrServiceMessages`(
)
BEGIN
    SELECT
        svc_message_id,
        svc_message_title,
        svc_message_txt,
        svc_message_author,
        svc_message_email,
        svc_message_submitdate,
        svc_message_active,
        svc_message_expires,
        svc_message_expirydate,
        svc_message_modifiedon,
        svc_message_modifiedby
    FROM `esolutionssvc`.`service_messages`
    WHERE svc_message_active = TRUE
    AND svc_message_expires >= UNIX_TIMESTAMP(NOW())
    OR svc_message_expires = 0
    ORDER BY svc_message_id DESC;
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