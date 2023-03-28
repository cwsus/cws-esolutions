--
-- CWSSEC / RESET_DATA
--
DELIMITER //

DROP TABLE IF EXISTS CWSSEC.RESET_DATA //

CREATE TABLE CWSSEC.RESET_DATA (
    CN VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    RESET_KEY VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    PRIMARY KEY (CN),
    CONSTRAINT FK_LGN_GUID
        FOREIGN KEY (CN)
        REFERENCES CWSSEC.USERS (CN)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    INDEX IDX_USERS (CN, RESET_KEY)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 ROW_FORMAT=COMPACT COLLATE utf8mb4_general_ci //
COMMIT //

GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON CWSSEC.* TO 'appadm'@'localhost' //
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON CWSSEC.* TO 'appadm'@'appsrv.lan' //

--
--
--
DROP PROCEDURE IF EXISTS CWSSEC.insertResetData //
DROP PROCEDURE IF EXISTS CWSSEC.getResetData //
DROP PROCEDURE IF EXISTS CWSSEC.removeResetData //

CREATE PROCEDURE CWSSEC.insertResetData(
    IN guid VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN resetId VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
)
BEGIN
    INSERT INTO CWSSEC.RESET_DATA
    (CN, RESET_KEY, CREATE_TIME)
    VALUES
    (guid, resetId, CURRENT_TIMESTAMP());

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE CWSSEC.getResetData(
    IN resetId VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN commonName VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
)
BEGIN
    SELECT CN, CREATE_TIME
    FROM CWSSEC.RESET_DATA
    WHERE RESET_KEY = resetId
    AND CN = commonName;
END //
COMMIT //

CREATE PROCEDURE CWSSEC.removeResetData(
    IN commonName VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    IN resetId VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
)
BEGIN
    DELETE FROM CWSSEC.RESET_DATA
    WHERE RESET_KEY = resetId
    AND CN = commonName;

    COMMIT;
END //
COMMIT //

GRANT EXECUTE ON PROCEDURE CWSSEC.insertResetData TO 'appadm'@'localhost' //
GRANT EXECUTE ON PROCEDURE CWSSEC.getResetData TO 'appadm'@'localhost' //
GRANT EXECUTE ON PROCEDURE CWSSEC.removeResetData TO 'appadm'@'localhost' //

GRANT EXECUTE ON PROCEDURE CWSSEC.insertResetData TO 'appadm'@'appsrv.lan' //
GRANT EXECUTE ON PROCEDURE CWSSEC.getResetData TO 'appadm'@'appsrv.lan' //
GRANT EXECUTE ON PROCEDURE CWSSEC.removeResetData TO 'appadm'@'appsrv.lan' //

DELIMITER ;
