--
-- Definition of table `cwssec`.`usr_audit`
--
DROP TABLE IF EXISTS `cwssec`.`usr_audit`;
CREATE TABLE `cwssec`.`usr_audit` (
    `usr_audit_sessionid` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_userid` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_userguid` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_role` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_applid` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_applname` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    `usr_audit_action` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_srcaddr` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_srchost` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    CONSTRAINT `FK_LGN_GUID`
        FOREIGN KEY (`usr_audit_userguid`)
        REFERENCES `cwssec`.`usr_lgn` (`CN`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    FULLTEXT KEY `audit_search` (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `cwssec`.`usr_audit` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `cwssec`.`getAuditEntryByAttribute`
--
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditEntryByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`getAuditEntryByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        usr_audit_sessionid,
        usr_audit_userid,
        usr_audit_userguid,
        usr_audit_role,
        usr_audit_applid,
        usr_audit_applname,
        usr_audit_timestamp,
        usr_audit_action,
        usr_audit_srcaddr,
        usr_audit_srchost,
    MATCH (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `cwssec`.`usr_audit`
    WHERE MATCH (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getAuditCount`
--
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`getAuditCount`(
    IN userguid VARCHAR(128)
)
BEGIN
    SELECT COUNT(*)
    FROM usr_audit
    WHERE usr_audit_userguid = userguid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getAuditInterval`
--
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditInterval`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`getAuditInterval`(
    IN userguid VARCHAR(128),
    IN startRow INT
)
BEGIN
    SELECT
        usr_audit_sessionid,
        usr_audit_userid,
        usr_audit_userguid,
        usr_audit_role,
        usr_audit_applid,
        usr_audit_applname,
        usr_audit_timestamp,
        usr_audit_action,
        usr_audit_srcaddr,
        usr_audit_srchost
    FROM usr_audit
    WHERE usr_audit_userguid = userguid
    ORDER BY usr_audit_timestamp DESC
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `insertAuditEntry`
--
DROP PROCEDURE IF EXISTS `cwssec`.`insertAuditEntry`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`insertAuditEntry`(
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
    INSERT INTO usr_audit (usr_audit_sessionid, usr_audit_userid, usr_audit_userguid, usr_audit_role, usr_audit_applid, usr_audit_applname, usr_audit_timestamp, usr_audit_action, usr_audit_srcaddr, usr_audit_srchost)
    VALUES (usersessid, username, userguid, userrole, applid, applname, UNIX_TIMESTAMP(NOW()), useraction, srcaddr, srchost);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
