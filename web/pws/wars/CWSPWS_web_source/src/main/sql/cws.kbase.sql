/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cws;

--
-- Definition of table `cws`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cws`.`articles`;
CREATE TABLE `cws`.`articles` (
    `kbase_page_hits` TINYINT NOT NULL default 0,
    `kbase_article_id` VARCHAR(100) NOT NULL default '',
    `kbase_article_createdate` BIGINT NOT NULL,
    `kbase_article_author` VARCHAR(45) NOT NULL default '',
    `kbase_article_keywords` VARCHAR(100) NOT NULL default '',
    `kbase_article_title` VARCHAR(100) NOT NULL default '',
    `kbase_article_symptoms` VARCHAR(100) NOT NULL default '',
    `kbase_article_cause` VARCHAR(100) NOT NULL default '',
    `kbase_article_resolution` TEXT NOT NULL,
    `kbase_article_status` INTEGER NOT NULL default 1,
    `kbase_article_reviewedby` VARCHAR(45),
    `kbase_article_revieweddate` BIGINT,
    `kbase_article_modifieddate` BIGINT,
    `kbase_article_modifiedby` VARCHAR(45),
    `kbase_article_author_email` VARCHAR(100) NOT NULL DEFAULT '',
    PRIMARY KEY  (`kbase_article_id`),
    FULLTEXT KEY `articles` (`kbase_article_id`, `kbase_article_keywords`, `kbase_article_title`, `kbase_article_symptoms`, `kbase_article_cause`, `kbase_article_resolution`, `kbase_article_author_email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cws`.`articles`
--
/*!40000 ALTER TABLE `cws`.`articles` DISABLE KEYS */;
/*!40000 ALTER TABLE `cws`.`articles` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `cws`.`search_kbase`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`search_kbase`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`search_kbase`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        kbase_article_id, kbase_article_title,
    MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution, kbase_article_author_email)
    AGAINST (searchTerms WITH QUERY EXPANSION) AS score
    FROM `cws`.`articles`
    WHERE MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution, kbase_article_author_email)
    AGAINST (searchTerms IN BOOLEAN MODE)
    AND kbase_article_status = 0;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`retrieve_top_articles`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`retrieve_top_articles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`retrieve_top_articles`(
)
BEGIN
    SELECT
        kbase_article_id,
        kbase_article_title
    FROM `articles`
    WHERE kbase_page_hits >= 10
    AND kbase_article_status = 0
    LIMIT 15;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`retrieve_article`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`retrieve_article`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `retrieve_article`(
    IN isApproval BOOLEAN,
    IN articleId VARCHAR(100)
)
BEGIN
    IF (isApproval)
    THEN
        SELECT
            kbase_page_hits, kbase_article_id, kbase_article_createdate, kbase_article_author,
            kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause,
            kbase_article_resolution, kbase_article_status, kbase_article_reviewedby, kbase_article_revieweddate,
            kbase_article_modifieddate, kbase_article_modifiedby, kbase_article_author_email
        FROM `articles`
        WHERE kbase_article_id = articleId
        AND kbase_article_status = 1;
    ELSE
        UPDATE `articles`
        SET kbase_page_hits = kbase_page_hits + 1
        WHERE kbase_article_id = articleId;

        SELECT
            kbase_page_hits, kbase_article_id, kbase_article_createdate, kbase_article_author,
            kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause,
            kbase_article_resolution, kbase_article_status, kbase_article_reviewedby, kbase_article_revieweddate,
            kbase_article_modifieddate, kbase_article_modifiedby, kbase_article_author_email
        FROM `articles`
        WHERE kbase_article_id = articleId
        AND kbase_article_status = 0;
    END IF;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`search_kbase`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`insert_article`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`insert_article`(
    IN articleId VARCHAR(45),
    IN author VARCHAR(45),
    IN authorEmail VARCHAR(100),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT
)
BEGIN
    INSERT INTO `cws`.`articles`
    (
        kbase_page_hits, kbase_article_id, kbase_article_createdate, kbase_article_author,
        kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause,
        kbase_article_resolution, kbase_article_status, kbase_article_author_email
    )
    VALUES
    (
        0, articleId, UNIX_TIMESTAMP(), author, keywords, title,
        symptoms, cause, resolution, 1, authorEmail
    );
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`update_article`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`update_article`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`update_article`(
    IN articleId VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT,
    IN modifiedBy VARCHAR(45)
)
BEGIN
    UPDATE `cws`.`articles`
    SET
        kbase_article_keywords = keywords,
        kbase_article_title = title,
        kbase_article_symptoms = symptoms,
        kbase_article_cause = cause,
        kbase_article_resolution = resolution,
        kbase_article_modifiedby = modifiedBy,
        kbase_article_modifieddate = UNIX_TIMESTAMP(),
        kbase_article_status = 1
    WHERE kbase_article_id = articleId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`disable_kbase_article`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`disable_kbase_article`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`disable_kbase_article`(
    IN articleId VARCHAR(45)
)
BEGIN
    UPDATE `cws`.`articles`
    SET kbase_article_status = 2
    WHERE kbase_article_id = articleId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`approve_kbase_article`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`approve_kbase_article`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`approve_kbase_article`(
    IN articleId VARCHAR(45),
    IN reviewedBy VARCHAR(45)
)
BEGIN
    UPDATE `cws`.`articles`
    SET
        kbase_article_status = 0,
        kbase_article_reviewedby = reviewedBy,
        kbase_article_revieweddate = UNIX_TIMESTAMP()
    WHERE kbase_article_id = articleId
    AND kbase_article_status = 1;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`retrieve_pending_articles`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`retrieve_pending_articles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`retrieve_pending_articles`(
    IN requestorId VARCHAR(100)
)
BEGIN
    SELECT kbase_article_id, kbase_article_title, kbase_article_createdate
    FROM `cws`.`articles`
    WHERE kbase_article_status = 1
    AND kbase_article_author != requestorId
    ORDER BY kbase_article_createdate DESC;
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