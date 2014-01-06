--
-- Definition of table esolutionssvc_history.service_messages
--
DROP TABLE IF EXISTS esolutionssvc_history.service_messages;
CREATE TABLE esolutionssvc_history.service_messages (
    ID VARCHAR(16) CHARACTER SET UTF8 NOT NULL,
    TITLE VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    MESSAGE TEXT CHARACTER SET UTF8 NOT NULL,
    AUTHOR VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    SUBMIT_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ACTIVE BOOLEAN NOT NULL DEFAULT TRUE,
    ALERT BOOLEAN NOT NULL DEFAULT FALSE,
    EXPIRES BOOLEAN NOT NULL DEFAULT FALSE,
    EXPIRES_ON TIMESTAMP,
    MODIFIED_ON TIMESTAMP,
    MODIFIED_BY VARCHAR(128) CHARACTER SET UTF8,
    PRIMARY KEY (ID, TITLE), -- prevent the same message from being submitted twice (we hope)
    FULLTEXT KEY FTK_svcMessages (ID, TITLE, MESSAGE, AUTHOR)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE esolutionssvc_history.service_messages CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure esolutionssvc_history.getMessagesByAttribute
--
DROP PROCEDURE IF EXISTS esolutionssvc_history.getMessagesByAttribute$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.getMessagesByAttribute(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY,
    MATCH (ID, TITLE, MESSAGE, AUTHOR)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM esolutionssvc_history.service_messages
    WHERE MATCH (ID, TITLE, MESSAGE, AUTHOR)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND ACTIVE  = TRUE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc_history.submitSvcMessage
--
DROP PROCEDURE IF EXISTS esolutionssvc_history.submitSvcMessage$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.submitSvcMessage(
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
    INSERT INTO esolutionssvc_history.service_messages
    (
        ID, TITLE, MESSAGE, AUTHOR, SUBMIT_DATE, ACTIVE , ALERT, EXPIRES, EXPIRES_ON
    )
    VALUES
    (
        messageId, messageTitle, messageText, messageAuthor, NOW(), active, isAlert, expiry, expiryDate
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc_history.updateServiceMessage
--
DROP PROCEDURE IF EXISTS esolutionssvc_history.updateServiceMessage$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.updateServiceMessage(
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
    UPDATE esolutionssvc_history.service_messages
    SET
        TITLE = messageTitle,
        MESSAGE = messageText,
        ACTIVE  = active,
        ALERT = isAlert,
        EXPIRES = expiry,
        EXPIRES_ON = expiryDate,
        MODIFIED_ON = NOW(),
        MODIFIED_BY = modifyAuthor
    WHERE ID = messageId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc_history.retrSvcMessages
--
DROP PROCEDURE IF EXISTS esolutionssvc_history.retrServiceMessage$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.retrServiceMessage(
    IN requestId VARCHAR(45)
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM esolutionssvc_history.service_messages
    WHERE ID = requestId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc_history.retrServiceMessages
--
COMMIT$$
DROP PROCEDURE IF EXISTS esolutionssvc_history.retrServiceMessages$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.retrServiceMessages(
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM esolutionssvc_history.service_messages
    WHERE ACTIVE  = TRUE
    AND ALERT = FALSE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure esolutionssvc_history.retrAlertMessages
--
DROP PROCEDURE IF EXISTS esolutionssvc_history.retrAlertMessages$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE esolutionssvc_history.retrAlertMessages(
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM esolutionssvc_history.service_messages
    WHERE ACTIVE  = TRUE
    AND ALERT = TRUE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
