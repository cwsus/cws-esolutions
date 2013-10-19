/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`service_requests`
--
DROP TABLE IF EXISTS `esolutionssvc`.`service_requests`;
CREATE TABLE `esolutionssvc`.`service_requests` (
    `SERVICE_REQUEST_ID` VARCHAR(32) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_AUTHOR` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_AUTHORADDR` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_INFORMATION` TEXT CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_SUBMITDATE` BIGINT NOT NULL,
    `SERVICE_REQUEST_STATUS` VARCHAR(32) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_ASSIGNEE` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_ASSIGNEEADDR` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `SERVICE_REQUEST_COMMENTS` TEXT CHARACTER SET UTF8,
    `SERVICE_REQUEST_CLOSEDATE` BIGINT,
    PRIMARY KEY (`SERVICE_REQUEST_ID`),
    FULLTEXT KEY `IDX_SEARCH` (`SERVICE_REQUEST_ID`,`SERVICE_REQUEST_AUTHOR`,`SERVICE_REQUEST_ASSIGNEE`,`SERVICE_REQUEST_STATUS`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

--
-- Dumping data for table `esolutionssvc`.`service_requests`
--
/*!40000 ALTER TABLE `esolutionssvc`.`service_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`service_requests` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`submitSvcRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`submitSvcMessage`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`submitSvcMessage`(
    IN serviceId VARCHAR(128),
    IN requestAuthor VARCHAR(100),
    IN authorEmail VARCHAR(45),
    IN requestInfo TEXT
)
BEGIN
    INSERT INTO `esolutionssvc`.`service_requests`
    (
        SERVICE_REQUEST_ID,
        SERVICE_REQUEST_AUTHOR,
        SERVICE_REQUEST_AUTHORADDR,
        SERVICE_REQUEST_SUBMITDATE,
        SERVICE_REQUEST_INFORMATION,
        SERVICE_REQUEST_STATUS
    )
    VALUES
    (
        serviceId,
        requestAuthor,
        authorEmail,
        UNIX_TIMESTAMP(),
        requestInfo,
        'OPEN'
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`assignServiceRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`assignServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`assignServiceRequest`(
    IN messageId VARCHAR(128),
    IN assignee VARCHAR(45),
    IN assigneeEmail VARCHAR(100)
)
BEGIN
    UPDATE `esolutionssvc`.`service_requests`
    SET
        SERVICE_REQUEST_AUTHOR = assignee,
        SERVICE_REQUEST_AUTHORADDR = assigneeEmail,
        SERVICE_REQUEST_STATUS = 'ASSIGNED'
    WHERE SERVICE_REQUEST_ID = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`acceptServiceRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`acceptServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`acceptServiceRequest`(
    IN messageId VARCHAR(128),
    IN comments TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`service_requests`
    SET
        SERVICE_REQUEST_STATUS = 'ACCEPTED',
        SERVICE_REQUEST_COMMENTS = comments,
        SERVICE_REQUEST_CLOSEDATE = UNIX_TIMESTAMP()
    WHERE SERVICE_REQUEST_ID = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`rejectServiceRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`rejectServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`rejectServiceRequest`(
    IN messageId VARCHAR(128),
    IN comments TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`service_requests`
    SET
        SERVICE_REQUEST_STATUS = 'REJECTED',
        SERVICE_REQUEST_COMMENTS = comments,
        SERVICE_REQUEST_CLOSEDATE = UNIX_TIMESTAMP()
    WHERE SERVICE_REQUEST_ID = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`completeServiceRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`completeServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`completeServiceRequest`(
    IN messageId VARCHAR(128),
    IN comments TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`service_requests`
    SET
        SERVICE_REQUEST_STATUS = 'COMPLETED',
        SERVICE_REQUEST_COMMENTS = comments,
        SERVICE_REQUEST_CLOSEDATE = UNIX_TIMESTAMP()
    WHERE SERVICE_REQUEST_ID = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`closeServiceRequest`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`closeServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`closeServiceRequest`(
    IN messageId VARCHAR(128),
    IN comments TEXT
)
BEGIN
    UPDATE `esolutionssvc`.`service_requests`
    SET
        SERVICE_REQUEST_STATUS = 'CLOSED',
        SERVICE_REQUEST_COMMENTS = comments,
        SERVICE_REQUEST_CLOSEDATE = UNIX_TIMESTAMP()
    WHERE SERVICE_REQUEST_ID = messageId;

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
