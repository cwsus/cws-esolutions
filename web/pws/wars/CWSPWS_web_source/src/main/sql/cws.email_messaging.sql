/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cws;

--
-- Definition of table `cws`.`email_service`
--
DROP TABLE IF EXISTS `cws`.`email_service`;
CREATE TABLE `cws`.`email_service` (
    `email_message_id` VARCHAR(100),
    `email_message_date` BIGINT NOT NULL,
    `email_message_from` VARCHAR(100),
	`email_message_subject` VARCHAR(100),
	`email_message_body` TEXT,
    PRIMARY KEY  (`email_message_id`),
    FULLTEXT KEY `email_search` (`email_message_id`, `email_message_from`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cws`.`email_service`
--
/*!40000 ALTER TABLE `cws`.`email_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `cws`.`email_service` ENABLE KEYS */;
COMMIT;	

--
-- Definition of procedure `cws`.`retrieveEmailCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`retrieveEmailCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`retrieveEmailCount`(
    IN messageId VARCHAR(100)
)
BEGIN
    SELECT
        email_message_id,
        email_message_from
    FROM `cws`.`email_service`
    WHERE `email_message_id` = messageId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`submitEmailMessage`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`submitEmailMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`submitEmailMessage`(
    IN messageId VARCHAR(100),
    IN messageFrom VARCHAR(100),
    IN messageAddr VARCHAR(100),
    IN messageSubject VARCHAR(100),
    IN messageContent TEXT
)
BEGIN
    INSERT INTO `esolutions`.`email_service`
        (email_message_id, email_message_date, email_message_from, email_message_addr, email_message_subject, email_message_content)
    VALUES
        (messageId, UNIX_TIMESTAMP(), messageFrom, messageAddr, messageSubject, messageContent);

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