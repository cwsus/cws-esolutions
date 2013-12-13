--
-- Definition of table `cwssec`.`usr_sec_ques`
--
DROP TABLE IF EXISTS `cwssec`.`usr_sec_ques`;
CREATE TABLE `cwssec`.`usr_sec_ques` (
    `usr_sec_ques_one` VARCHAR(100) NOT NULL,
    `usr_sec_ques_two` VARCHAR(100) NOT NULL,
    `usr_sec_ques_three` VARCHAR(100) NOT NULL,
    `usr_sec_ques_four` VARCHAR(100) NOT NULL,
    `usr_sec_ques_five` VARCHAR(100) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `cwssec`.`usr_sec_ques` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

--
-- add in default questions
--
/*!40000 ALTER TABLE `cwssec`.`usr_sec_ques` DISABLE KEYS */;
INSERT INTO `cwssec`.`usr_sec_ques` (usr_sec_ques_one, usr_sec_ques_two, usr_sec_ques_three, usr_sec_ques_four, usr_sec_ques_five)
VALUES ('What is your mother''s maiden name ?', 'What is your favourite cartoon ?', 'What is your favourite car ?', 'What is your least favourite colour ?', 'Who was your childhood best friend ?');
/*!40000 ALTER TABLE `cwssec`.`usr_sec_ques` ENABLE KEYS */;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `cwssec`.`retrieve_user_questions`
--
DROP PROCEDURE IF EXISTS `cwssec`.`retrieve_user_questions`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `cwssec`.`retrieve_user_questions`(
)
BEGIN
    SELECT *
    FROM `cwssec`.`usr_sec_ques`;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
