DELIMITER //

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ //

DROP PROCEDURE IF EXISTS `CWSSEC`.`getUserByAttribute` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`listUserAccounts` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`updateUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`updateUserPassword` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`resetUserPassword` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`showUserAccounts` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`showUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`removeUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`performAuthentication` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`loginSuccess` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`verifySecurityQuestions` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addOrUpdateSecurityQuestions` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`lockUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`unlockUserAccount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`modifyUserSuspension` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addPublicKey` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrPublicKey` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`passwordExpirationNotifier` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addUserSalt` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrUserSalt` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`updateUserSalt` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`removeUserData` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`insertResetData` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`getResetData` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`verifySmsCodeForReset` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`listActiveResetRequests` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`removeResetData` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrieve_user_questions` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`getAuditEntryByAttribute` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`getAuditCount` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`getAuditInterval` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`insertAuditEntry` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addUserKeys` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrUserKeys` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`deleteUserKeys` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrAvailableServices` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`addServiceToUser` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`removeServiceFromUser` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`isUserAuthorizedForService` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`retrAuthorizedServices` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`listServicesForUser` //
DROP PROCEDURE IF EXISTS `CWSSEC`.`performSuccessfulLogin` //

CREATE PROCEDURE `CWSSEC`.`getUserByAttribute`(
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
    FROM `CWSSEC`.`USERS`
    WHERE MATCH (`UID`, `CWSROLE`, `GIVENNAME`, `SN`, `EMAIL`, `CN`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`listUserAccounts`(
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
    FROM `CWSSEC`.`USERS`;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addUserAccount`(
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

    INSERT INTO USERS
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
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`updateUserAccount`(
    IN commonName VARCHAR(128),
    IN cwsRole VARCHAR(45),
    IN surname VARCHAR(100),
    IN givenName VARCHAR(100),
    IN emailAddr VARCHAR(100),
    IN displayName VARCHAR(100)
)
BEGIN
    UPDATE USERS
    SET
        CWSROLE = cwsRole,
        sn = surname,
        GIVENNAME = givenName,
        EMAIL = emailAddr,
        DISPLAYNAME = displayName
    WHERE cn = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`updateUserPassword`(
    IN commonName VARCHAR(128),
    IN currentPassword VARCHAR(255),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE USERS
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE USERPASSWORD = currentPassword
    AND CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`resetUserPassword`(
    IN commonName VARCHAR(128),
    IN newPassword VARCHAR(255)
)
BEGIN
    UPDATE USERS
    SET
        USERPASSWORD = newPassword,
        CWSEXPIRYDATE = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`showUserAccounts`(
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
    FROM USERS;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`showUserAccount`(
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
    FROM USERS
    WHERE cn = commonName;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`removeUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    DELETE FROM `CWSSEC`.`USERS`
    WHERE cn = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`performAuthentication`(
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
    FROM `CWSSEC`.`USERS`
    WHERE CN = guid
    AND UID = username
    AND USERPASSWORD = password;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`loginSuccess`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100)
)
BEGIN
    UPDATE CWSSEC.USERS
    SET
        CWSLASTLOGIN = unix_timestamp(now()),
        CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName
    AND UID = username;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`verifySecurityQuestions`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    SELECT COUNT(CN)
    FROM `CWSSEC`.`USERS`
    WHERE CWSSECANS1 = secAnswerOne
    AND CWSSECANS2 = secAnswerTwo
    AND CN = commonName;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addOrUpdateSecurityQuestions`(
    IN commonName VARCHAR(128),
    IN userName VARCHAR(100),
    IN userPassword VARCHAR(255),
    IN secQuestionOne VARCHAR(60),
    IN secQuestionTwo VARCHAR(60),
    IN secAnswerOne VARCHAR(255),
    IN secAnswerTwo VARCHAR(255)
)
BEGIN
    UPDATE `CWSSEC`.`USERS`
    SET
        CWSSECQ1 = secQuestionOne,
        CWSSECQ2 = secQuestionTwo,
        CWSSECANS1 = secAnswerOne,
        CWSSECANS2 = secAnswerTwo
    WHERE UID = userName
    AND CN = commonName
    AND USERPASSWORD = userPassword;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`lockUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT CWSFAILEDPWDCOUNT
    FROM `CWSSEC`.`USERS`
    WHERE CN = commonName
    INTO @CURRENT_COUNT;

    UPDATE `CWSSEC`.`USERS`
    SET CWSFAILEDPWDCOUNT = @CURRENT_COUNT + 1
    WHERE CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`unlockUserAccount`(
    IN commonName VARCHAR(128)
)
BEGIN
    UPDATE `CWSSEC`.`USERS`
    SET CWSFAILEDPWDCOUNT = 0
    WHERE CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`modifyUserSuspension`(
    IN commonName VARCHAR(128),
    IN isSuspended BOOLEAN
)
BEGIN
    UPDATE `CWSSEC`.`USERS`
    SET CWSISSUSPENDED = isSuspended
    WHERE CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addPublicKey`(
    IN commonName VARCHAR(128),
    IN publicKey VARBINARY(4352)
)
BEGIN
    UPDATE `CWSSEC`.`USERS`
    SET CWSPUBLICKEY = publicKey
    WHERE CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrPublicKey`(
    IN commonName VARCHAR(128)
)
BEGIN
    SELECT CWSPUBLICKEY
    FROM `CWSSEC`.`USERS`
    WHERE CN = commonName;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`passwordExpirationNotifier`(
)
BEGIN
    SELECT
        UID,
        SN,
        GIVENNAME,
        CWSEXPIRYDATE,
        EMAIL
    FROM USERS;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addUserSalt`(
    IN guid VARCHAR(100),
    IN salt VARCHAR(128),
    in sType VARCHAR(15)
)
BEGIN
    INSERT INTO `CWSSEC`.`LOGON_DATA` (CN, SALT, SALT_TYPE)
    VALUES (guid, salt, sType);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrUserSalt`(
    IN guid VARCHAR(100),
    IN sType VARCHAR(15)
)
BEGIN
    SELECT SALT
    FROM `CWSSEC`.`LOGON_DATA`
    WHERE CN = guid
    AND SALT_TYPE = sType;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`updateUserSalt`(
    IN guid VARCHAR(100),
    IN saltValue VARCHAR(64),
    IN sType VARCHAR(15)
)
BEGIN
    UPDATE `CWSSEC`.`LOGON_DATA`
    SET SALT = saltValue
    WHERE CN = guid
    AND SALT_TYPE = sType;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`removeUserData`(
    IN guid VARCHAR(100)
)
BEGIN
    DELETE FROM `CWSSEC`.`LOGON_DATA`
    WHERE CN = guid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`insertResetData`(
    IN guid VARCHAR(128),
    IN resetId VARCHAR(128),
    IN timeCreated BIGINT(20),
    IN smsId VARCHAR(8)
)
BEGIN
    INSERT INTO `CWSSEC`.`RESET_DATA`
    (CN, RESET_KEY, CREATE_TIME, SMS_CODE)
    VALUES
    (guid, resetId, timeCreated, SMS_CODE);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`getResetData`(
    IN resetId VARCHAR(128)
)
BEGIN
    SELECT CN, CREATE_TIME
    FROM RESET_DATA
    WHERE RESET_KEY = resetId;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`verifySmsCodeForReset`(
    IN guid VARCHAR(128),
    IN resetId VARCHAR(128),
    IN smsId VARCHAR(8)
)
BEGIN
    SELECT RESET_KEY, CREATE_TIME
    FROM RESET_DATA
    WHERE CN = guid
    AND RESET_KEY = resetId
    AND SMS_CODE = smsId;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`listActiveResetRequests`(
)
BEGIN
    SELECT CN, RESET_KEY, CREATE_TIME
    FROM RESET_DATA;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`removeResetData`(
    IN commonName VARCHAR(128),
    IN resetId VARCHAR(128)
)
BEGIN
    DELETE FROM RESET_DATA
    WHERE RESET_KEY = resetId
    AND CN = commonName;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrieve_user_questions`(
)
BEGIN
    SELECT *
    FROM `CWSSEC`.`SECURITY_QUESTIONS`;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`getAuditEntryByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        SESSION_ID,
        USERNAME,
        CN,
        ROLE,
        APPLICATION_ID,
        APPLICATION_NAME,
        REQUEST_TIMESTAMP,
        ACTION,
        SOURCE_ADDRESS,
        SOURCE_HOSTNAME,
    MATCH (`USERNAME`, `CN`, `ROLE`, `SOURCE_ADDRESS`, `SOURCE_HOSTNAME`, `ACTION`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `CWSSEC`.`AUDIT`
    WHERE MATCH (`USERNAME`, `CN`, `ROLE`, `SOURCE_ADDRESS`, `SOURCE_HOSTNAME`, `ACTION`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`getAuditCount`(
    IN userguid VARCHAR(128)
)
BEGIN
    SELECT COUNT(*)
    FROM AUDIT
    WHERE CN = userguid;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`getAuditInterval`(
    IN userguid VARCHAR(128),
    IN startRow INT
)
BEGIN
    SELECT
        SESSION_ID,
        USERNAME,
        CN,
        ROLE,
        APPLICATION_ID,
        APPLICATION_NAME,
        REQUEST_TIMESTAMP,
        ACTION,
        SOURCE_ADDRESS,
        SOURCE_HOSTNAME
    FROM AUDIT
    WHERE CN = userguid
    ORDER BY REQUEST_TIMESTAMP DESC
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`insertAuditEntry`(
    IN usersessid VARCHAR(100),
    IN username VARCHAR(45),
    IN userguid VARCHAR(128),
    IN userrole VARCHAR(45),
    IN applid VARCHAR(128),
    IN applname VARCHAR(128),
    IN useraction VARCHAR(45),
    IN srcaddr VARCHAR(45),
    IN srchost VARCHAR(128)
)
BEGIN
    INSERT INTO AUDIT (SESSION_ID, USERNAME, CN, ROLE, APPLICATION_ID, APPLICATION_NAME, REQUEST_TIMESTAMP, ACTION, SOURCE_ADDRESS, SOURCE_HOSTNAME)
    VALUES (usersessid, username, userguid, userrole, applid, applname, CURRENT_TIMESTAMP(), useraction, srcaddr, srchost);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addUserKeys`(
    IN userGuid VARCHAR(45),
    IN privKey VARBINARY(4352)
)
BEGIN
    INSERT INTO KEY_DATA (CN, PRIVATE_KEY)
    VALUES (userGuid, privKey);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrUserKeys`(
    IN userGuid VARCHAR(45)
)
BEGIN
    SELECT PRIVATE_KEY
    FROM KEY_DATA
    WHERE CN = userGuid;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`deleteUserKeys`(
    IN userGuid VARCHAR(45)
)
BEGIN
    DELETE FROM `CWSSEC`.`KEY_DATA`
    WHERE CN = userGuid;

    UPDATE `CWSSEC`.`USERS`
    SET CWSPUBLICKEY = NULL
    WHERE CN = userGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrAvailableServices`(
)
BEGIN
    SELECT URI, ID
    FROM `CWSSEC`.`USER_SERVICES`
    ORDER BY ID ASC;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`addServiceToUser`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    INSERT INTO `CWSSEC`.`SERVICE_MAP` (CN, ID)
    VALUES (guid, serviceId);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`removeServiceFromUser`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    DELETE FROM `CWSSEC`.`SERVICE_MAP`
    WHERE CN = guid
    AND ID = serviceId;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`isUserAuthorizedForService`(
    IN guid VARCHAR(128),
    IN serviceId VARCHAR(128)
)
BEGIN
    SELECT COUNT(ID)
    FROM `CWSSEC`.`SERVICE_MAP`
    WHERE CN = guid
    AND ID = serviceId;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`retrAuthorizedServices`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        T1.ID,
        T2.NAME
    FROM SERVICE_MAP T1
    INNER JOIN USER_SERVICES T2
    ON T1.ID = T2.ID
    WHERE CN = guid;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`listServicesForUser`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT ID
    FROM SERVICE_MAP
    WHERE cn = userGuid;
END //
COMMIT //

CREATE PROCEDURE `CWSSEC`.`performSuccessfulLogin`(
    IN userId VARCHAR(45),
    IN userGuid VARCHAR(128),
    IN lockCounter MEDIUMINT,
    IN loginTimestamp TIMESTAMP
)
BEGIN
    UPDATE USERS
    SET
        CWSFAILEDPWDCOUNT = lockCounter,
        CWSLASTLOGIN = CURRENT_TIMESTAMP
    WHERE CN = userGuid
    AND UID = userId;

    COMMIT;
END //
COMMIT //



/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */ //

DELIMITER ;
