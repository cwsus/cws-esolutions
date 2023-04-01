DELIMITER //

DROP TABLE IF EXISTS ESOLUTIONS.DNSSERVICE //

CREATE TABLE ESOLUTIONS.DNSSERVICE (
    ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    ZONE_FILE VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL, -- required for all entries, this will act as a correlator for apex/sub
    APEX_RECORD BOOLEAN NOT NULL DEFAULT FALSE,
    ORIGIN VARCHAR(126) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT ".", -- required for all entries
    TIMETOLIVE INTEGER, -- required for apex records and srv records (we're going to re-use it)
    HOSTNAME VARCHAR(126) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL, -- required for apex records - 63 per label, including TLD this is 126
    RECORDOWNER VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for apex records
    HOSTMASTER VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for apex records
    RECORDSERIAL INTEGER, -- required for apex records
    REFRESH INTEGER, -- required for apex records
    RETRY INTEGER, -- required for apex records
    EXPIRES INTEGER, -- required for apex records
    CACHETIME INTEGER, -- required for apex records
    CLASS_NAME VARCHAR(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT "IN", -- required for all records
    CLASS_TYPE VARCHAR(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for all records
    SRVPORT INTEGER, -- required for srv records
    WEIGHT INTEGER, -- required for srv and mx records
    SERVICE VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for srv records
    PROTOCOL VARCHAR(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for srv records
    PRIORITY INTEGER DEFAULT 10, -- required for srv records
    PRIMARY_TARGET VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- required for all records
    SECONDARY_TARGET VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- secondary target list, used for failover
    TERTIARY_TARGET VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci, -- tertiary target list, used for failover
    PRIMARY KEY (ID),
    FULLTEXT KEY IDX_SEARCH (ZONE_FILE, ORIGIN, HOSTNAME, RECORDOWNER, CLASS_TYPE, SERVICE, PRIMARY_TARGET)
) ENGINE=InnoDB CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COLLATE utf8mb4_0900_ai_ci //
COMMIT //

GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON ESOLUTIONS.* TO 'appadm'@'localhost' //
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON ESOLUTIONS.* TO 'appadm'@'appsrv.lan' //

--
--
--
DROP PROCEDURE IF EXISTS ESOLUTIONS.getRecordByAttribute //
DROP PROCEDURE IF EXISTS ESOLUTIONS.insertApex //
DROP PROCEDURE IF EXISTS ESOLUTIONS.insertRecord //

CREATE PROCEDURE ESOLUTIONS.getRecordByAttribute(
    IN attributeName VARCHAR(126)
)
BEGIN
    SELECT
        ZONE_FILE,
        APEX_RECORD,
        ORIGIN,
        TIMETOLIVE,
        HOSTNAME,
        OWNER,
        HOSTMASTER,
        SERIAL,
        REFRESH,
        RETRY,
        EXPIRES,
        CACHETIME,
        CLASS_NAME,
        CLASS_TYPE,
        PORT,
        WEIGHT,
        SERVICE,
        PROTOCOL,
        PRIORITY,
        PRIMARY_TARGET,
        SECONDARY_TARGET,
        TERTIARY_TARGET,
    MATCH (ZONE_FILE, ORIGIN, HOSTNAME, OWNER, CLASS_TYPE, SERVICE, PRIMARY_TARGET)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM ESOLUTIONS.DNSSERVICE
    WHERE MATCH (ZONE_FILE, ORIGIN, HOSTNAME, OWNER, CLASS_TYPE, SERVICE, PRIMARY_TARGET)
    AGAINST (+attributeName IN BOOLEAN MODE)
    ORDER BY APEX_RECORD DESC, ORIGIN ASC;
END //
COMMIT //

CREATE PROCEDURE ESOLUTIONS.insertApex(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN timeToLive INTEGER,
    IN hostname VARCHAR(126),
    IN masterNameserver VARCHAR(255),
    IN hostmaster VARCHAR(255),
    IN serial INTEGER,
    IN refresh INTEGER,
    IN retry INTEGER,
    IN expiry INTEGER,
    IN cacheTime INTEGER
)
BEGIN
    INSERT INTO ESOLUTIONS.DNSSERVICE
    (
        ZONE_FILE, APEX_RECORD, ORIGIN,
        TIMETOLIVE, HOSTNAME, OWNER, HOSTMASTER,
        SERIAL, REFRESH, RETRY, EXPIRES, CACHETIME
    )
    VALUES
    (
        zoneFile, true, origin, timeToLive,
        hostname, masterNameserver, hostmaster, serial,
        refresh, retry, expiry, cacheTime
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE ESOLUTIONS.insertRecord(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN hostname VARCHAR(126),
    IN rrClass VARCHAR(8),
    IN rrType VARCHAR(11),
    IN portNumber INTEGER,
    IN weight INTEGER,
    IN service VARCHAR(10),
    IN protocol VARCHAR(6),
    IN priority INTEGER,
    IN target VARCHAR(255),
    IN secondary VARCHAR(255),
    IN tertiary VARCHAR(255)
)
BEGIN
    INSERT INTO ESOLUTIONS.DNSSERVICE
    (
        ZONE_FILE, APEX_RECORD, ORIGIN,
        HOSTNAME, CLASS_NAME, CLASS_TYPE, PORT, WEIGHT,
        SERVICE, PROTOCOL, PRIORITY, PRIMARY_TARGET,
        SECONDARY_TARGET, TERTIARY_TARGET
    )
    VALUES
    (
        zoneFile, false, origin,
        hostname, rrClass, rrType, portNumber,
        weight, service, protocol, priority, target,
        secondary, tertiary
    );

    COMMIT;
END //
COMMIT //

GRANT EXECUTE ON PROCEDURE ESOLUTIONS.getRecordByAttribute TO 'appadm'@'localhost' //
GRANT EXECUTE ON PROCEDURE ESOLUTIONS.insertApex TO 'appadm'@'localhost' //
GRANT EXECUTE ON PROCEDURE ESOLUTIONS.insertRecord TO 'appadm'@'localhost' //

GRANT EXECUTE ON PROCEDURE ESOLUTIONS.getRecordByAttribute TO 'appadm'@'appsrv.lan' //
GRANT EXECUTE ON PROCEDURE ESOLUTIONS.insertApex TO 'appadm'@'appsrv.lan' //
GRANT EXECUTE ON PROCEDURE ESOLUTIONS.insertRecord TO 'appadm'@'appsrv.lan' //

DELIMITER ;
