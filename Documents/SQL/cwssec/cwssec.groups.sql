﻿--
-- Definition of table CWSSEC.GROUPS
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS CWSSEC.GROUPS;
CREATE TABLE CWSSEC.GROUPS (
    ID INT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL,
    SERVICES TEXT,
    PRIMARY KEY (ID, NAME),
    INDEX IDX_GROUPS (ID, NAME)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE CWSSEC.GROUPS CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

--
-- add in default groups
--
/*!40000 ALTER TABLE CWSSEC.GROUPS DISABLE KEYS */;
-- Application Management/Operations
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Application Manager', '3653a95b-848a-4a4f-bcf7-5ed914575bc6, 097e3027-52ac-4fed-91ae-5b3d512d8d2e, 0c8ffe71-0c43-47b1-948e-2c8df8713e05, a94bb3c1-8d38-41c2-9771-a2ccd4b46366, e760a5b4-9497-4873-baca-fefb486edbea, d411855b-b317-4787-9751-dd87055a150a, 7e305298-7c16-4521-9b44-22c586f94581, 0db909ed-f97e-4257-817a-646a722e8b61');
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Application Operator', 'a94bb3c1-8d38-41c2-9771-a2ccd4b46366, e760a5b4-9497-4873-baca-fefb486edbea, d411855b-b317-4787-9751-dd87055a150a, e579c503-9e8d-4bbe-b25b-f1787bce9dba');
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Application Deployer', 'a94bb3c1-8d38-41c2-9771-a2ccd4b46366, e760a5b4-9497-4873-baca-fefb486edbea, d411855b-b317-4787-9751-dd87055a150a, 7e305298-7c16-4521-9b44-22c586f94581, 0db909ed-f97e-4257-817a-646a722e8b61, e579c503-9e8d-4bbe-b25b-f1787bce9dba');

-- DNS Management/Operations
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('DNS Manager', '281d02e7-f948-4830-afc4-2386a9ab66c5, d92f8884-972c-4a7f-9bc5-4103cb57914f, f5da369f-7a5e-4250-9383-372e58221edf, c3a01b03-d49f-4588-9484-132853f72a98');
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('DNS Operator', '281d02e7-f948-4830-afc4-2386a9ab66c5, c3a01b03-d49f-4588-9484-132853f72a98');

-- Server Management/Operations
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Server Manager', '44fe9373-781a-45a1-80c2-01de0b903083, 5bac5eb9-249b-4745-8fb5-936f7f4784d5, c5101c2d-7835-4eae-972d-b32cb6eae39, 0a4f0f7d-a411-4397-ba6d-5e6b45e7f004, e760a5b4-9497-4873-baca-fefb486edbea, f007bde9-133d-4d42-83dc-3dcfe9383cd8, 0ba28df3-b93e-42dd-af49-5e43e72cccd4, 1408c2b1-e726-4d7f-901f-b46bbd70cce9, ef97ad95-688a-4626-b397-bdbf27b40e1b, 82065610-cc25-4bcd-9521-da771042f98a');
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Server Operator', '0a4f0f7d-a411-4397-ba6d-5e6b45e7f004, e760a5b4-9497-4873-baca-fefb486edbea, f007bde9-133d-4d42-83dc-3dcfe9383cd8, 0ba28df3-b93e-42dd-af49-5e43e72cccd4, 1408c2b1-e726-4d7f-901f-b46bbd70cce9, ef97ad95-688a-4626-b397-bdbf27b40e1b, 82065610-cc25-4bcd-9521-da771042f98a, e579c503-9e8d-4bbe-b25b-f1787bce9dba');

-- Service Management/Operations
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Service Manager', '9b249a67-8839-4d86-b273-121a7a42cbea, 0bae5166-d178-4538-876a-b2f7f666c99e, df6d9651-f5e9-4ab4-9092-a9d03f8a8ff0, 8986de0b-1c88-44c4-a08c-8cf7baf1b2eb, bfa8782e-fcd7-43a4-9bf3-ab2178f5e39f, e579c503-9e8d-4bbe-b25b-f1787bce9dba, d16ccf16-604e-45fe-8241-feaf62b5fd00');
INSERT INTO CWSSEC.GROUPS (NAME, SERVICES) VALUES ('Service Operator', '8986de0b-1c88-44c4-a08c-8cf7baf1b2eb, bfa8782e-fcd7-43a4-9bf3-ab2178f5e39f, e579c503-9e8d-4bbe-b25b-f1787bce9dba, d16ccf16-604e-45fe-8241-feaf62b5fd00');
/*!40000 ALTER TABLE CWSSEC.GROUPS ENABLE KEYS */;
COMMIT;

DELIMITER $$

--
-- Definition of procedure CWSSEC.addGroup
--
DROP PROCEDURE IF EXISTS CWSSEC.addGroup $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.addGroup(
    IN id VARCHAR(128),
    IN name VARCHAR(50)
)
BEGIN
    INSERT INTO CWSSEC.GROUPS (ID, NAME)
    VALUES (id, name);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.addServiceToGroup
--
DROP PROCEDURE IF EXISTS CWSSEC.addServiceToGroup $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.addServiceToGroup(
    IN id VARCHAR(128),
    IN service VARCHAR(128)
)
BEGIN
    SET @currentServices = (SELECT SERVICES FROM CWSSEC.GROUPS WHERE ID = id);

    UPDATE CWSSEC.GROUPS
    SET SERVICES = @currentServices + service
    WHERE ID = id;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.removeServiceFromGroup
--
DROP PROCEDURE IF EXISTS CWSSEC.removeServiceFromGroup $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.removeServiceFromGroup(
    IN id VARCHAR(128),
    IN service VARCHAR(128)
)
BEGIN
    UPDATE CWSSEC.GROUPS
    SET SERVICES = REPLACE(SERVICE, service, '')
    WHERE ID = id;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
