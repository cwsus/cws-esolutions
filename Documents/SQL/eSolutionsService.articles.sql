--
-- Definition of table `esolutionssvc`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `esolutionssvc`.`articles`;
CREATE TABLE `esolutionssvc`.`articles` (
    `kbase_page_hits` TINYINT NOT NULL default 0,
    `kbase_article_id` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_createdate` BIGINT NOT NULL,
    `kbase_article_author` VARCHAR(45) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_keywords` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_title` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_symptoms` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_cause` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `kbase_article_resolution` TEXT NOT NULL,
    `kbase_article_status` VARCHAR(15) CHARACTER SET UTF8 NOT NULL DEFAULT 'NEW',
    `kbase_article_reviewedby` VARCHAR(45) CHARACTER SET UTF8,
    `kbase_article_revieweddate` BIGINT,
    `kbase_article_modifieddate` BIGINT,
    `kbase_article_modifiedby` VARCHAR(45) CHARACTER SET UTF8,
    PRIMARY KEY  (`kbase_article_id`),
    FULLTEXT KEY `articles` (`kbase_article_id`, `kbase_article_keywords`, `kbase_article_title`, `kbase_article_symptoms`, `kbase_article_cause`, `kbase_article_resolution`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;

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
CREATE PROCEDURE `esolutionssvc`.`getArticleByAttribute`(
    IN searchTerms VARCHAR(100),
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
    MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc`.`articles`
    WHERE MATCH (kbase_article_id, kbase_article_keywords, kbase_article_title, kbase_article_symptoms, kbase_article_cause, kbase_article_resolution)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND kbase_article_status = 'APPROVED'
    LIMIT startRow, 20;
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
CREATE PROCEDURE `esolutionssvc`.`retrTopArticles`(
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
        kbase_article_modifiedby
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
CREATE PROCEDURE `retrArticle`(
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
            kbase_article_modifiedby
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
            kbase_article_modifiedby
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
CREATE PROCEDURE `esolutionssvc`.`addNewArticle`(
    IN articleId VARCHAR(45),
    IN author VARCHAR(45),
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
        kbase_article_resolution, kbase_article_status
    )
    VALUES
    (
        0, articleId, UNIX_TIMESTAMP(), author, keywords, title,
        symptoms, cause, resolution, 'NEW'
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
CREATE PROCEDURE `esolutionssvc`.`updateArticle`(
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
CREATE PROCEDURE `esolutionssvc`.`updateArticleStatus`(
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
-- Definition of procedure `getArticleCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `esolutionssvc`.`getArticleCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc`.`getArticleCount`(
    IN reqType VARCHAR(45)
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc`.`articles`
    WHERE kbase_article_status = reqType
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
CREATE PROCEDURE `esolutionssvc`.`retrPendingArticles`(
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
        kbase_article_modifiedby
    FROM `esolutionssvc`.`articles`
    WHERE kbase_article_status IN ('NEW', 'REJECTED', 'REVIEW')
    AND kbase_article_author != requestorId
    ORDER BY kbase_article_createdate DESC
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;
