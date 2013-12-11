--
-- Definition of table `usr_lgn`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cwssec`.`usr_lgn`;
CREATE TABLE `cwssec`.`usr_lgn` (
    `CN` VARCHAR(45) NOT NULL,
    `UID` VARCHAR(45) NOT NULL DEFAULT 'USERNAME',
    `USERPASSWORD` VARCHAR(255) NOT NULL,
    `CWSROLE` VARCHAR(45) NOT NULL,
    `CWSFAILEDPWDCOUNT` INT(10) UNSIGNED DEFAULT '0',
    `CWSLASTLOGIN` BIGINT(20) UNSIGNED DEFAULT '0',
    `CWSISSUSPENDED` BOOLEAN NOT NULL DEFAULT FALSE,
    `CWSISOLRSETUP` BOOLEAN NOT NULL DEFAULT TRUE,
    `CWSISOLRLOCKED` BOOLEAN NOT NULL DEFAULT FALSE,
    `CWSISTCACCEPTED` BOOLEAN NOT NULL DEFAULT FALSE,
    `SN` VARCHAR(100) NOT NULL,
    `GIVENNAME` VARCHAR(100) NOT NULL,
    `DISPLAYNAME` VARCHAR(100) NOT NULL DEFAULT 'DISPLAY NAME',
    `EMAIL` VARCHAR(50) NOT NULL,
    `CWSEXPIRYDATE` BIGINT(20) UNSIGNED NOT NULL DEFAULT '0',
    `CWSSECQ1` VARCHAR(60) NOT NULL DEFAULT 'QUESTION 1',
    `CWSSECQ2` VARCHAR(60) NOT NULL DEFAULT 'QUESTION 2',
    `CWSSECANS1` VARCHAR(255) NOT NULL DEFAULT 'ANSWER 1',
    `CWSSECANS2` VARCHAR(255) NOT NULL DEFAULT 'ANSWER 2',
    `CWSPUBLICKEY` VARBINARY(4352),
    PRIMARY KEY  (`UID`, `CN`),
    UNIQUE KEY `USERID` (`UID`),
    KEY `CN` USING BTREE (`CN`),
    FULLTEXT KEY `USER_SEARCH` (`UID`, `CWSROLE`, `GIVENNAME`, `SN`, `EMAIL`, `CN`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

--
-- Definition of procedure `cwssec`.`getUserByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`getUserByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`getUserByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        CN,
        UID,
        GIVENNAME,
        SN,
        DISPLAYNAME,
        EMAIL,
        CWSROLE,
        CWSFAILEDPWDCOUNT,
        CWSLASTLOGIN,
        CWSEXPIRYDATE,
        CWSISSUSPENDED,
        CWSISOLRSETUP,
        CWSISOLRLOCKED,
        CWSISTCACCEPTED,
        CWSSECQ1,
        CWSSECQ2,
        CWSPUBLICKEY,
    MATCH (`UID`, `CWSROLE`, `GIVENNAME`, `SN`, `EMAIL`, `CN`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `cwssec`.`usr_lgn`
    WHERE MATCH (`UID`, `CWSROLE`, `GIVENNAME`, `SN`, `EMAIL`, `CN`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `cwssec`.`listUserAccounts`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`listUserAccounts`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`listUserAccounts`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        CN,
        UID,
        GIVENNAME,
        SN,
        DISPLAYNAME,
        EMAIL,
        CWSROLE,
        CWSFAILEDPWDCOUNT,
        CWSLASTLOGIN,
        CWSEXPIRYDATE,
        CWSISSUSPENDED,
        CWSISOLRSETUP,
        CWSISOLRLOCKED,
        CWSISTCACCEPTED,
        CWSSECQ1,
        CWSSECQ2,
        CWSPUBLICKEY
    FROM `cwssec`.`usr_lgn`;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

--
-- Definition of procedure `cwssec`.`addUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`addUserAccount`(
    IN uid VARCHAR(45),
    IN userPassword VARCHAR(255),
    IN cwsRole VARCHAR(45),
    IN surname VARCHAR(100),
    IN givenName VARCHAR(100),
    IN emailAddr VARCHAR(50),
    IN commonName VARCHAR(128),
    IN displayName VARCHAR(100)
)
BEGIN
    SELECT unix_timestamp(now()) INTO @EXPIRY_TIME;

    INSERT INTO usr_lgn
    (
        UID, USERPASSWORD, CWSROLE, SN, GIVENNAME,
        CWSEXPIRYDATE, EMAIL, CN, DISPLAYNAME
    )
    VALUES
    (
        uid, userPassword, cwsRole, surname, givenName,
        unix_timestamp(now()), emailAddr, commonName, displayName
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`updateUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`updateUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`updateUserAccount`(
    IN commonName VARCHAR(128),
    IN cwsRole VARCHAR(45),
    IN surname VARCHAR(100),
    IN givenName VARCHAR(100),
    IN emailAddr VARCHAR(100),
    IN displayName VARCHAR(100)
)
BEGIN
    UPDATE usr_lgn
    SET
        CWSROLE = cwsRole,
        sn = surname,
        GIVENNAME = givenName,
        EMAIL = emailAddr,
        DISPLAYNAME = displayName
    WHERE cn = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`updateUserPassword`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`updateUserPassword`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`updateUserPassword`(
    IN commonName VARCHAR(128),
    IN currentPassword VARCHAR(255),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE usr_lgn
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE USERPASSWORD = currentPassword
    AND CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`resetUserPassword`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`resetUserPassword`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`resetUserPassword`(
    IN commonName VARCHAR(128),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE usr_lgn
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `showUserAccounts`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`showUserAccounts`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`showUserAccounts`(
)
BEGIN
    SELECT
        UID,
        CWSROLE,
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
        CWSISTCACCEPTED,
        CWSPUBLICKEY
    FROM usr_lgn;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `showUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`showUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`showUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT
        UID,
        CWSROLE,
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
        CWSISTCACCEPTED,
        CWSPUBLICKEY
    FROM usr_lgn
    WHERE cn = commonName;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `removeUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`removeUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`removeUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    DELETE FROM `cwssec`.`usr_lgn`
    WHERE cn = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`performAuthentication`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`performAuthentication`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`performAuthentication`(
    IN guid VARCHAR(128),
    IN username VARCHAR(100),
    IN password VARCHAR(255)
)
BEGIN
    SELECT DISTINCT
        CN,
        UID,
        GIVENNAME,
        SN,
        DISPLAYNAME,
        EMAIL,
        CWSROLE,
        CWSFAILEDPWDCOUNT,
        CWSLASTLOGIN,
        CWSEXPIRYDATE,
        CWSISSUSPENDED,
        CWSISOLRSETUP,
        CWSISOLRLOCKED,
        CWSISTCACCEPTED,
        CWSPUBLICKEY
    FROM `cwssec`.`usr_lgn`
    WHERE CN = guid
    AND UID = username
    AND USERPASSWORD = password;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`loginSuccess`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`loginSuccess`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`loginSuccess`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100)
)
BEGIN
    UPDATE cwssec.usr_lgn
    SET
        CWSLASTLOGIN = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName
    AND UID = username;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`verifySecurityQuestions`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`verifySecurityQuestions`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`verifySecurityQuestions`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    SELECT COUNT(CN)
    FROM `cwssec`.`usr_lgn`
    WHERE CWSSECANS1 = secAnswerOne
    AND CWSSECANS2 = secAnswerTwo
    AND CN = commonName;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`addOrUpdateSecurityQuestions`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addOrUpdateSecurityQuestions`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`addOrUpdateSecurityQuestions`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN userPassword VARCHAR(255),
    IN secQuestionOne VARCHAR(60),
    IN secQuestionTwo VARCHAR(60),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    UPDATE `cwssec`.`usr_lgn`
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

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`lockUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`lockUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`lockUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT CWSFAILEDPWDCOUNT
    FROM `cwssec`.`usr_lgn`
    WHERE CN = commonName
    INTO @CURRENT_COUNT;

    UPDATE `cwssec`.`usr_lgn`
    SET CWSFAILEDPWDCOUNT = @CURRENT_COUNT + 1
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`unlockUserAccount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`unlockUserAccount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`unlockUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    UPDATE `cwssec`.`usr_lgn`
    SET CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`modifyUserSuspension`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`modifyUserSuspension`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`modifyUserSuspension`(
    IN commonName VARCHAR(128),
    IN isSuspended BOOLEAN
)
BEGIN
    UPDATE `cwssec`.`usr_lgn`
    SET CWSISSUSPENDED = isSuspended
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`addPublicKey`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`addPublicKey`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`addPublicKey`(
    IN commonName VARCHAR(128),
    IN publicKey VARBINARY(4352)
)
BEGIN
    UPDATE `cwssec`.`usr_lgn`
    SET CWSPUBLICKEY = publicKey
    WHERE CN = commonName;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`retrPublicKey`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`retrPublicKey`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`retrPublicKey`(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT CWSPUBLICKEY
    FROM `cwssec`.`usr_lgn`
    WHERE CN = commonName;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cwssec`.`quartzDataRetr`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`passwordExpirationNotifier`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`passwordExpirationNotifier`(
)
BEGIN
    SELECT
        UID,
        SN,
        GIVENNAME,
        CWSEXPIRYDATE,
        EMAIL
    FROM usr_lgn;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;
