--
-- Definition of table CWSSEC.SERVICES
-- REFERENCE TABLE
--
DROP TABLE IF EXISTS CWSSEC.SERVICES;
CREATE TABLE CWSSEC.SERVICES (
    ID VARCHAR(128) NOT NULL,
    NAME VARCHAR(128) NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    PRIMARY KEY (ID),
    INDEX IDX_SERVICES (ID, NAME)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE CWSSEC.SERVICES CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

--
-- add in default services
--
/*!40000 ALTER TABLE CWSSEC.USER_SERVICES DISABLE KEYS */;
-- Application Management/Operations
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('3653a95b-848a-4a4f-bcf7-5ed914575bc6', 'Add Application', 'Allows the ability to add a new application');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('097e3027-52ac-4fed-91ae-5b3d512d8d2e', 'Update Application', 'Allows the ability to modify an existing application');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('0c8ffe71-0c43-47b1-948e-2c8df8713e05', 'Delete Application', 'Allows the ability to remove an existing application');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('a94bb3c1-8d38-41c2-9771-a2ccd4b46366', 'List Applications', 'Allows the ability to list available applications');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('e760a5b4-9497-4873-baca-fefb486edbea', 'Search Applications', 'Allows the ability to search for applications');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('d411855b-b317-4787-9751-dd87055a150a', 'Retrieve Application', 'Allows the ability to retrieve data for an installed application');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('7e305298-7c16-4521-9b44-22c586f94581', 'Application File Request', 'Allows the ability to retrieve application file data from a remote system.');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('0db909ed-f97e-4257-817a-646a722e8b61', 'Deploy Application', 'Allows the ability to deploy an application to remote systems.');

-- DNS Management/Operations
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('281d02e7-f948-4830-afc4-2386a9ab66c5', 'DNS Lookup', 'Allows the ability to perform DNS service lookups');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('d92f8884-972c-4a7f-9bc5-4103cb57914f', 'Add DNS', 'Allows the ability to add new DNS records');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('f5da369f-7a5e-4250-9383-372e58221edf', 'Deploy DNS', 'Allows the ability to deploy new DNS records to DNS systems');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('c3a01b03-d49f-4588-9484-132853f72a98', 'DNS Failover', 'Allows the ability to perform DNS service failovers');

-- Server Management/Operations
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('44fe9373-781a-45a1-80c2-01de0b903083', 'Add Server', 'Allows the ability to add a new server');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('5bac5eb9-249b-4745-8fb5-936f7f4784d5', 'Update Server', 'Allows the ability to modify an existing server');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('c5101c2d-7835-4eae-972d-b32cb6eae392', 'Delete Server', 'Allows the ability to remove an existing server');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('0a4f0f7d-a411-4397-ba6d-5e6b45e7f004', 'List Servers', 'Allows the ability to list available servers');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('e769a5b4-9497-4873-baca-fefb486edbea', 'Search Servers', 'Allows the ability to search for servers');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('f007bde9-133d-4d42-83dc-3dcfe9383cd8', 'Retrieve Server', 'Allows the ability to retrieve data for a given server');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('0ba28df3-b93e-42dd-af49-5e43e72cccd4', 'System Check', 'Allows the ability to perform various system checks');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('1408c2b1-e726-4d7f-901f-b46bbd70cce9', 'List Virtual Machines', 'Allows the ability to list available Virtual Machines');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('ef97ad95-688a-4626-b397-bdbf27b40e1b', 'Start Virtual Machine', 'Allows the ability to stop a Virtual Machine');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('82065610-cc25-4bcd-9521-da771042f98a', 'Stop Virtual Machine', 'Allows the ability to start a Virtual Machine');

-- Service Management/Operations
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('9b249a67-8839-4d86-b273-121a7a42cbea', 'Add Service', 'Allows the ability to add a new Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('0bae5166-d178-4538-876a-b2f7f666c99e', 'Update Service', 'Allows the ability to modify an existing Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('df6d9651-f5e9-4ab4-9092-a9d03f8a8ff0', 'Delete Service', 'Allows the ability to remove an existing Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('8986de0b-1c88-44c4-a08c-8cf7baf1b2eb', 'List Service', 'Allows the ability to list available Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('bfa8782e-fcd7-43a4-9bf3-ab2178f5e39f', 'Search Service', 'Allows the ability to search for Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('e579c503-9e8d-4bbe-b25b-f1787bce9dba', 'Retrieve Service', 'Allows the ability to retrieve data for a given Service');
INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION) VALUES ('d16ccf16-604e-45fe-8241-feaf62b5fd00', 'Disable Service', 'Allows the ability to disable a given service');
/*!40000 ALTER TABLE CWSSEC.USER_SERVICES ENABLE KEYS */;
COMMIT;

DELIMITER $$

--
-- Definition of procedure CWSSEC.addService
--
DROP PROCEDURE IF EXISTS CWSSEC.addService $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.addService(
    IN id VARCHAR(128),
    IN name VARCHAR(128),
    IN description TEXT
)
BEGIN
    INSERT INTO CWSSEC.SERVICES (ID, NAME, DESCRIPTION)
    VALUES (id, name, description);

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.removeService
--
DROP PROCEDURE IF EXISTS CWSSEC.removeService $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.removeService(
    IN id VARCHAR(128)
)
BEGIN
    DELETE FROM CWSSEC.SERVICES
    WHERE ID = id;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure CWSSEC.listServicesForGroup
--
DROP PROCEDURE IF EXISTS CWSSEC.listServicesForGroup $$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE CWSSEC.listServicesForGroup(
    IN name VARCHAR(128)
)
BEGIN
    SELECT SERVICES
    FROM CWSSEC.GROUPS
    WHERE NAME = name;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
