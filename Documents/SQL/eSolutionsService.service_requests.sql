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
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc`.`service_requests` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc`.`submitSvcRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`submitSvcRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`submitSvcRequest`(
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
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`assignServiceRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`assignServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`assignServiceRequest`(
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
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`acceptServiceRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`acceptServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`acceptServiceRequest`(
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
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`rejectServiceRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`rejectServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`rejectServiceRequest`(
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
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`completeServiceRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`completeServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`completeServiceRequest`(
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
COMMIT$$

--
-- Definition of procedure `esolutionssvc`.`closeServiceRequest`
--
DROP PROCEDURE IF EXISTS `esolutionssvc`.`closeServiceRequest`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`closeServiceRequest`(
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
COMMIT$$

DELIMITER ;
COMMIT;
