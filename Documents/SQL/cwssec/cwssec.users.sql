--
-- Definition of table USERS
-- DATA TABLE
--
DROP TABLE IF EXISTS CWSSEC.USERS;
CREATE TABLE CWSSEC.USERS (
    CN VARCHAR(128) NOT NULL,
    UID VARCHAR(50) NOT NULL,
    USERPASSWORD VARCHAR(255) NOT NULL,
    CWSFAILEDPWDCOUNT INT NOT NULL DEFAULT 0,
    CWSLASTLOGIN TIMESTAMP,
    CWSISSUSPENDED BOOLEAN NOT NULL DEFAULT FALSE,
    CWSISOLRSETUP BOOLEAN NOT NULL DEFAULT TRUE,
    CWSISOLRLOCKED BOOLEAN NOT NULL DEFAULT FALSE,
    SN VARCHAR(100) NOT NULL,
    GIVENNAME VARCHAR(100) NOT NULL,
    DISPLAYNAME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(50) NOT NULL,
    TELEPHONE VARCHAR(12) NOT NULL,
    PAGER VARCHAR(12) NOT NULL,
    CWSEXPIRYDATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    CWSSECQ1 VARCHAR(60) NOT NULL,
    CWSSECQ2 VARCHAR(60) NOT NULL,
    CWSSECANS1 VARCHAR(255) NOT NULL,
    CWSSECANS2 VARCHAR(255) NOT NULL,
    MEMBEROF TEXT NOT NULL,
    PRIMARY KEY (CN),
    UNIQUE (UID),
    INDEX IDX_USERS (CN, UID, EMAIL),
    FULLTEXT KEY FT_USERS (UID, GIVENNAME, SN, EMAIL, CN)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE CWSSEC.USERS CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure CWSSEC.getUserByAttribute
--
DROP PROCEDURE IF EXISTS CWSSEC.getUserByAttribute$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.getUserByAttribute(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        CN,
        UID,
    MATCH (UID, GIVENNAME, SN, EMAIL, CN)
    AGAINST (+attributeName WITH QUERY EXPANSION) AS score
    FROM CWSSEC.USERS
    WHERE MATCH (UID, GIVENNAME, SN, EMAIL, CN)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.listUserAccounts
--
DROP PROCEDURE IF EXISTS CWSSEC.listUserAccounts$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.listUserAccounts(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        CN,
        UID
    FROM CWSSEC.USERS;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.addUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.addUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.addUserAccount(
    IN guid VARCHAR(128),
    IN username VARCHAR(50),
    IN password VARCHAR(255),
    IN suspended BOOLEAN,
    IN surname VARCHAR(100),
    IN givenname VARCHAR(100),
    IN displayname VARCHAR(100),
    IN email VARCHAR(50)
)
BEGIN
    INSERT INTO CWSSEC.USERS
    (
        CN, UID, USERPASSWORD, CWSISSUSPENDED, CWSISOLRSETUP,
        CWSISOLRLOCKED, SN, GIVENNAME, DISPLAYNAME, EMAIL, CWSEXPIRYDATE
    )
    VALUES
    (
        guid, username, password, suspended, TRUE,
        FALSE, surname, givenname, displayname, email, unix_timestamp(now())
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.updateUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.updateUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.updateUserAccount(
    IN commonName VARCHAR(128),
    IN surname VARCHAR(100),
    IN givenName VARCHAR(100),
    IN emailAddr VARCHAR(100),
    IN displayName VARCHAR(100)
)
BEGIN
    UPDATE USERS
    SET
        sn = surname,
        GIVENNAME = givenName,
        EMAIL = emailAddr,
        DISPLAYNAME = displayName
    WHERE cn = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.updateUserPassword
--
DROP PROCEDURE IF EXISTS CWSSEC.updateUserPassword$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.updateUserPassword(
    IN commonName VARCHAR(128),
    IN expiry INT,
    IN currentPassword VARCHAR(255),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE USERS
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL expiry DAY),
        CWSFAILEDPWDCOUNT = 0
    WHERE USERPASSWORD = currentPassword
    AND CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.resetUserPassword
--
DROP PROCEDURE IF EXISTS CWSSEC.resetUserPassword$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.resetUserPassword(
    IN commonName VARCHAR(128),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE USERS
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = CURRENT_TIMESTAMP(),
        CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure showUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.showUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.showUserAccount(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT
        UID,
        CWSFAILEDPWDCOUNT,
        CWSLASTLOGIN,
        SN,
        GIVENNAME,
        CWSEXPIRYDATE,
        EMAIL,
        CWSISSUSPENDED,
        CN,
        CWSISOLRSETUP,
        CWSISOLRLOCKED,
        DISPLAYNAME,
        MEMBEROF
    FROM USERS
    WHERE cn = commonName;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure removeUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.removeUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.removeUserAccount(
    IN commonName VARCHAR(128)
)
BEGIN
    DELETE FROM CWSSEC.USERS
    WHERE cn = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.performAuthentication
--
DROP PROCEDURE IF EXISTS CWSSEC.performAuthentication$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.performAuthentication(
    IN username VARCHAR(100),
    IN password VARCHAR(255)
)
BEGIN
    SET @guid := (SELECT DISTINCT CN FROM CWSSEC.USERS WHERE UID = username;

    IF ((SELECT COUNT(@guid)) = 1)
    THEN
        SET @count := (SELECT DISTINCT CN
            FROM CWSSEC.USERS
            WHERE UID = username AND
            USERPASSWORD = password);

        IF ((SELECT COUNT(@count)) = 1)
        THEN
            UPDATE CWSSEC.USERS
            SET CWSFAILEDPWDCOUNT = 0
            WHERE CN = @guid
            AND UID = username;

            SELECT DISTINCT
                CN,
                UID,
                GIVENNAME,
                SN,
                DISPLAYNAME,
                EMAIL,
                CWSFAILEDPWDCOUNT,
                CWSLASTLOGIN,
                UNIX_TIMESTAMP(CWSEXPIRYDATE),
                CWSISSUSPENDED,
                CWSISOLRSETUP,
                CWSISOLRLOCKED,
                MEMBEROF
            FROM CWSSEC.USERS
            WHERE CN = @guid
            AND UID = username;
        ELSE
            SET @lockCount := (SELECT CWSFAILEDPWDCOUNT
                FROM CWSSEC.USERS
                WHERE CN = @guid
                AND UID = username);

            UPDATE CWSSEC.USERS
            SET CWSFAILEDPWDCOUNT = @lockCount + 1
            WHERE CN = @guid
            AND UID = username;
        END IF;
    END IF;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.verifySecurityQuestions
--
DROP PROCEDURE IF EXISTS CWSSEC.verifySecurityQuestions$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.verifySecurityQuestions(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    SELECT COUNT(CN)
    FROM CWSSEC.USERS
    WHERE CWSSECANS1 = secAnswerOne
    AND CWSSECANS2 = secAnswerTwo
    AND CN = commonName;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.addOrUpdateSecurityQuestions
--
DROP PROCEDURE IF EXISTS CWSSEC.addOrUpdateSecurityQuestions$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.addOrUpdateSecurityQuestions(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN userPassword VARCHAR(255),
    IN secQuestionOne VARCHAR(60),
    IN secQuestionTwo VARCHAR(60),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    UPDATE CWSSEC.USERS
    SET
        CWSSECQ1 = secQuestionOne,
        CWSSECQ2 = secQuestionTwo,
        CWSSECANS1 = secAnswerOne,
        CWSSECANS2 = secAnswerTwo
    WHERE UID = userName
    AND CN = commonName
    AND USERPASSWORD = userPassword;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.lockUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.lockUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.lockUserAccount(
    IN commonName VARCHAR(128)
)
BEGIN
    UPDATE CWSSEC.USERS
    SET CWSFAILEDPWDCOUNT = 3
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.unlockUserAccount
--
DROP PROCEDURE IF EXISTS CWSSEC.unlockUserAccount$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.unlockUserAccount(
    IN commonName VARCHAR(128)
)
BEGIN
    UPDATE CWSSEC.USERS
    SET CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.modifyUserSuspension
--
DROP PROCEDURE IF EXISTS CWSSEC.modifyUserSuspension$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.modifyUserSuspension(
    IN commonName VARCHAR(128),
    IN isSuspended BOOLEAN
)
BEGIN
    UPDATE CWSSEC.USERS
    SET CWSISSUSPENDED = isSuspended
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.passwordExpirationNotifier
--
DROP PROCEDURE IF EXISTS CWSSEC.passwordExpirationNotifier$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.passwordExpirationNotifier(
)
BEGIN
    SELECT
        UID,
        SN,
        GIVENNAME,
        CWSEXPIRYDATE,
        EMAIL
    FROM USERS;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
