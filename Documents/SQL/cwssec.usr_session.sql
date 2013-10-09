/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_session`
--
DROP TABLE IF EXISTS `cwssec`.`usr_session`;
CREATE TABLE `cwssec`.`usr_session` (
    `session_id` VARCHAR(100) NOT NULL default '',
    `user_id` VARCHAR(45) NOT NULL,
    `user_cn` VARCHAR(45) NOT NULL,
    `create_date` TIMESTAMP NOT NULL,
    `last_accessed` TIMESTAMP NOT NULL,
    `is_active` BOOLEAN NOT NULL,
    `app_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY  (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cwssec`.`usr_session`
--

/*!40000 ALTER TABLE `cwssec`.`usr_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `cwssec`.`usr_session` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`addUserSession`
--

DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addUserSession` $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`addUserSession`(
    IN sessionId VARCHAR(100),
    IN userId VARCHAR(45),
    IN userSecId VARCHAR(45),
    IN isActive BOOLEAN,
    IN appName VARCHAR(45)
)
BEGIN
    INSERT INTO usr_session
        (session_id, user_id, user_cn, create_date, last_accessed, is_active, app_name)
    VALUES
        (sessionId, userId, userSecId, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), isActive, appName);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrUserSession`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrUserSession`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrUserSession`(
    IN sessionId VARCHAR(100),
    IN secid VARCHAR(45),
    IN appName VARCHAR(45)
)
BEGIN
    DECLARE sessionCount INT;

    SELECT COUNT(*)
    INTO sessionCount
    FROM usr_session
    WHERE session_id = sessionId
    AND user_cn = secid
    AND app_name = appName
    AND last_accessed <= DATE_ADD(NOW(), INTERVAL 15 MINUTE);

    IF (@sessionCount = 1)
    THEN
        UPDATE usr_session
        SET last_accessed = NOW()
        WHERE session_id = sessionId
        AND user_cn = secid
        AND app_name = appName;

        COMMIT;
    END IF;

    SELECT @sessionCount AS sessioncount;
END $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`removeUserSession`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeUserSession`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`removeUserSession`(
    IN userId VARCHAR(45),
    IN userSecId VARCHAR(45),
    IN sessionId VARCHAR(100),
    IN appName VARCHAR(45)
)
BEGIN
    DELETE FROM cwssec.usr_session
    WHERE user_id = userId
    AND app_name = appName
    AND user_cn = userSecId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of event `cwssec`.`expireResetData`
--
DROP EVENT IF EXISTS `cwssec`.`expireSession`;
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */;
CREATE DEFINER=`appuser`@`localhost` EVENT `cwssec`.`expireSession`
ON SCHEDULE EVERY 15 MINUTE
DO
	DELETE FROM `cwssec`.`usr_session`
	WHERE createTime >= CURRENT_TIMESTAMP;

	COMMIT;

COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;