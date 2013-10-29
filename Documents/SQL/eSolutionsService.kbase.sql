/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE esolutionssvc;

--
-- Definition of table `esolutionssvc`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `esolutionssvc`.`articles`;
CREATE TABLE `esolutionssvc`.`articles` (
    `kbase_page_hits` TINYINT NOT NULL default 0,
    `kbase_article_id` VARCHAR(100) NOT NULL default '',
    `kbase_article_createdate` BIGINT NOT NULL,
    `kbase_article_author` VARCHAR(45) NOT NULL default '',
    `kbase_article_keywords` VARCHAR(100) NOT NULL default '',
    `kbase_article_title` VARCHAR(100) NOT NULL default '',
    `kbase_article_symptoms` VARCHAR(100) NOT NULL default '',
    `kbase_article_cause` VARCHAR(100) NOT NULL default '',
    `kbase_article_resolution` TEXT NOT NULL,
    `kbase_article_status` VARCHAR(15) NOT NULL DEFAULT 'NEW',
    `kbase_article_reviewedby` VARCHAR(45),
    `kbase_article_revieweddate` BIGINT,
    `kbase_article_modifieddate` BIGINT,
    `kbase_article_modifiedby` VARCHAR(45),
    `kbase_article_author_email` VARCHAR(100) NOT NULL DEFAULT '',
    PRIMARY KEY  (`kbase_article_id`),
    FULLTEXT KEY `articles` (`kbase_article_id`, `kbase_article_keywords`, `kbase_article_title`, `kbase_article_symptoms`, `kbase_article_cause`, `kbase_article_resolution`, `kbase_article_author_email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `esolutionssvc`.`articles`
--
/*!40000 ALTER TABLE `esolutionssvc`.`articles` DISABLE KEYS */;
/*!40000 ALTER TABLE `esolutionssvc`.`articles` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `esolutionssvc`.`getArticleByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getArticleByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getArticleByAttribute`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        kbase_page_hits,
        kbase_article_id,
        kbase_article_createdate,
        kbase_article_author,
        kbase_article_keywords,
        kbase_article_title,
        kbase_article_symptoms,
        kbase_article_cause,
        kbase_article_resolution,
        kbase_article_status,
        kbase_article_reviewedby,
        kbase_article_revieweddate,
        kbase_article_modifieddate,
        kbase_article_modifiedby,
        kbase_article_author_email,
    MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution, kbase_article_author_email)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`articles`
    WHERE MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution, kbase_article_author_email)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND kbase_article_status = 'APPROVED';
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrTopArticles`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrTopArticles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrTopArticles`(
)
BEGIN
    SELECT
        kbase_page_hits,
        kbase_article_id,
        kbase_article_createdate,
        kbase_article_author,
        kbase_article_keywords,
        kbase_article_title,
        kbase_article_symptoms,
        kbase_article_cause,
        kbase_article_resolution,
        kbase_article_status,
        kbase_article_reviewedby,
        kbase_article_revieweddate,
        kbase_article_modifieddate,
        kbase_article_modifiedby,
        kbase_article_author_email
    FROM `articles`
    WHERE kbase_page_hits >= 10
    AND kbase_article_status = 'APPROVED'
    LIMIT 15;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrArticle`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `retrArticle`(
    IN articleId VARCHAR(100),
    IN isApproval BOOLEAN
)
BEGIN
    IF (isApproval)
    THEN
        SELECT
            kbase_page_hits,
            kbase_article_id,
            kbase_article_createdate,
            kbase_article_author,
            kbase_article_keywords,
            kbase_article_title,
            kbase_article_symptoms,
            kbase_article_cause,
            kbase_article_resolution,
            kbase_article_status,
            kbase_article_reviewedby,
            kbase_article_revieweddate,
            kbase_article_modifieddate,
            kbase_article_modifiedby,
            kbase_article_author_email
        FROM `articles`
        WHERE kbase_article_id = articleId
        AND kbase_article_status IN ('NEW', 'REVIEW');
    ELSE
        UPDATE `articles`
        SET kbase_page_hits = kbase_page_hits + 1
        WHERE kbase_article_id = articleId;

		COMMIT;

        SELECT
            kbase_page_hits,
            kbase_article_id,
            kbase_article_createdate,
            kbase_article_author,
            kbase_article_keywords,
            kbase_article_title,
            kbase_article_symptoms,
            kbase_article_cause,
            kbase_article_resolution,
            kbase_article_status,
            kbase_article_reviewedby,
            kbase_article_revieweddate,
            kbase_article_modifieddate,
            kbase_article_modifiedby,
            kbase_article_author_email
        FROM `articles`
        WHERE kbase_article_id = articleId
        AND kbase_article_status = 'APPROVED';
    END IF;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`addNewArticle`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`addNewArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`addNewArticle`(
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
    INSERT INTO `esolutionssvc`.`articles`
    (
        kbase_page_hits, kbase_article_id, kbase_article_createdate, kbase_article_author,
        kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause,
        kbase_article_resolution, kbase_article_status, kbase_article_author_email
    )
    VALUES
    (
        0, articleId, UNIX_TIMESTAMP(), author, keywords, title,
        symptoms, cause, resolution, 'NEW', authorEmail
    );

	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`updateArticle`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updateArticle`(
    IN articleId VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT,
    IN modifiedBy VARCHAR(45)
)
BEGIN
    UPDATE `esolutionssvc`.`articles`
    SET
        kbase_article_keywords = keywords,
        kbase_article_title = title,
        kbase_article_symptoms = symptoms,
        kbase_article_cause = cause,
        kbase_article_resolution = resolution,
        kbase_article_modifiedby = modifiedBy,
        kbase_article_modifieddate = UNIX_TIMESTAMP(),
        kbase_article_status = 'NEW'
    WHERE kbase_article_id = articleId;

	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`updateArticleStatus`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`updateArticleStatus`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`updateArticleStatus`(
    IN articleId VARCHAR(45),
	IN modifiedBy VARCHAR(45),
	IN articleStatus VARCHAR(15)
)
BEGIN
	UPDATE `esolutionssvc`.`articles`
	SET
        kbase_article_status = articleStatus,
        kbase_article_modifiedby = modifiedBy,
        kbase_article_modifieddate = UNIX_TIMESTAMP(),
        kbase_article_reviewedby = modifiedBy,
        kbase_article_revieweddate = UNIX_TIMESTAMP()
    WHERE kbase_article_id = articleId;

	COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `getPendingArticleCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getPendingArticleCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`getPendingArticleCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`articles`
    WHERE kbase_article_status IN ('NEW', 'REJECTED', 'REVIEW')
    AND kbase_article_author != requestorId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `esolutionssvc`.`retrPendingArticles`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`retrPendingArticles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `esolutionssvc`.`retrPendingArticles`(
    IN requestorId VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        kbase_page_hits,
        kbase_article_id,
        kbase_article_createdate,
        kbase_article_author,
        kbase_article_keywords,
        kbase_article_title,
        kbase_article_symptoms,
        kbase_article_cause,
        kbase_article_resolution,
        kbase_article_status,
        kbase_article_reviewedby,
        kbase_article_revieweddate,
        kbase_article_modifieddate,
        kbase_article_modifiedby,
        kbase_article_author_email
    FROM `esolutionssvc`.`articles`
    WHERE kbase_article_status IN ('NEW', 'REJECTED', 'REVIEW')
    AND kbase_article_author != requestorId
    ORDER BY kbase_article_createdate DESC
    LIMIT startRow, 20;
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
