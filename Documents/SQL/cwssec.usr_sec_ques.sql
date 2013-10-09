/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_sec_ques`
--

DROP TABLE IF EXISTS `cwssec`.`usr_sec_ques`;

COMMIT;

CREATE TABLE `cwssec`.`usr_sec_ques` (
    `usr_sec_ques_one` VARCHAR(100) NOT NULL,
    `usr_sec_ques_two` VARCHAR(100) NOT NULL,
    `usr_sec_ques_three` VARCHAR(100) NOT NULL,
    `usr_sec_ques_four` VARCHAR(100) NOT NULL,
    `usr_sec_ques_five` VARCHAR(100) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

COMMIT;

--
-- Dumping data for table `cwssec`.`usr_sec_ques`
--

/*!40000 ALTER TABLE `cwssec`.`usr_sec_ques` DISABLE KEYS */;
INSERT INTO `cwssec`.`usr_sec_ques`
    (usr_sec_ques_one, usr_sec_ques_two, usr_sec_ques_three, usr_sec_ques_four, usr_sec_ques_five)
VALUES
    ('What is your mother\'s maiden name ?', 'What is your favourite cartoon ?', 'What is your favourite car ?', 'What is your least favourite colour ?', 'Who was your childhood best friend ?');
/*!40000 ALTER TABLE `cwssec`.`usr_sec_ques` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `cwssec`.`retrieve_user_questions`
--

DROP PROCEDURE IF EXISTS `cwssec`.`retrieve_user_questions`;

COMMIT;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`retrieve_user_questions`(
)
BEGIN
    SELECT *
    FROM `cwssec`.`usr_sec_ques`;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;