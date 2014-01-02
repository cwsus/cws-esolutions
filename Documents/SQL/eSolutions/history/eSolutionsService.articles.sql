--
-- Definition of table `esolutionssvc_history`.`articles`
-- DATA TABLE
--
DROP TABLE IF EXISTS `esolutionssvc_history`.`articles`;
CREATE TABLE `esolutionssvc_history`.`articles` (
    `ID` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `HITS` TINYINT NOT NULL default 0,
    `CREATE_DATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    `AUTHOR` VARCHAR(45) CHARACTER SET UTF8 NOT NULL default '',
    `KEYWORDS` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `TITLE` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `SYMPTOMS` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `CAUSE` VARCHAR(100) CHARACTER SET UTF8 NOT NULL default '',
    `RESOLUTION` TEXT NOT NULL,
    `STATUS` VARCHAR(15) CHARACTER SET UTF8 NOT NULL DEFAULT 'NEW',
    `REVIEWED_BY` VARCHAR(45) CHARACTER SET UTF8,
    `REVIEW_DATE` TIMESTAMP,
    `MODIFIED_DATE` TIMESTAMP,
    `MODIFIED_BY` VARCHAR(45) CHARACTER SET UTF8,
    PRIMARY KEY  (`ID`),
    FULLTEXT KEY `articles` (`ID`, `KEYWORDS`, `TITLE`, `SYMPTOMS`, `CAUSE`, `RESOLUTION`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8 ROW_FORMAT=COMPACT COLLATE UTF8_GENERAL_CI;
COMMIT;

ALTER TABLE `esolutionssvc_history`.`articles` CONVERT TO CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
COMMIT;

DELIMITER $$

--
-- Definition of procedure `esolutionssvc_history`.`getArticleByAttribute`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getArticleByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getArticleByAttribute`(
    IN searchTerms VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY,
    MATCH (ID, KEYWORDS, TITLE, SYMPTOMS, CAUSE, RESOLUTION)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `esolutionssvc_history`.`articles`
    WHERE MATCH (ID, KEYWORDS, TITLE, SYMPTOMS, CAUSE, RESOLUTION)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND STATUS = 'APPROVED'
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`retrTopArticles`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retrTopArticles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`retrTopArticles`(
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY
    FROM `articles`
    WHERE HITS >= 10
    AND STATUS = 'APPROVED'
    LIMIT 15;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`retrArticle`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retrArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `retrArticle`(
    IN articleId VARCHAR(100),
    IN isApproval BOOLEAN
)
BEGIN
    IF (isApproval)
    THEN
        SELECT
            HITS,
            ID,
            CREATE_DATE,
            AUTHOR,
            KEYWORDS,
            TITLE,
            SYMPTOMS,
            CAUSE,
            RESOLUTION,
            STATUS,
            REVIEWED_BY,
            REVIEW_DATE,
            MODIFIED_DATE,
            MODIFIED_BY
        FROM `articles`
        WHERE ID = articleId
        AND STATUS IN ('NEW', 'REVIEW');
    ELSE
        UPDATE `articles`
        SET HITS = HITS + 1
        WHERE ID = articleId;

        COMMIT;

        SELECT
            HITS,
            ID,
            CREATE_DATE,
            AUTHOR,
            KEYWORDS,
            TITLE,
            SYMPTOMS,
            CAUSE,
            RESOLUTION,
            STATUS,
            REVIEWED_BY,
            REVIEW_DATE,
            MODIFIED_DATE,
            MODIFIED_BY
        FROM `articles`
        WHERE ID = articleId
        AND STATUS = 'APPROVED';
    END IF;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`addNewArticle`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`addNewArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`addNewArticle`(
    IN articleId VARCHAR(45),
    IN author VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT
)
BEGIN
    INSERT INTO `esolutionssvc_history`.`articles`
    (
        HITS, ID, CREATE_DATE, AUTHOR,
        KEYWORDS, TITLE, SYMPTOMS, CAUSE,
        RESOLUTION, STATUS
    )
    VALUES
    (
        0, articleId, UNIX_TIMESTAMP(), author, keywords, title,
        symptoms, cause, resolution, 'NEW'
    );

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`updateArticle`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`updateArticle`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`updateArticle`(
    IN articleId VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT,
    IN modifiedBy VARCHAR(45)
)
BEGIN
    UPDATE `esolutionssvc_history`.`articles`
    SET
        KEYWORDS = keywords,
        TITLE = title,
        SYMPTOMS = symptoms,
        CAUSE = cause,
        RESOLUTION = resolution,
        MODIFIED_BY = modifiedBy,
        MODIFIED_DATE = UNIX_TIMESTAMP(),
        STATUS = 'NEW'
    WHERE ID = articleId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`updateArticleStatus`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`updateArticleStatus`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`updateArticleStatus`(
    IN articleId VARCHAR(45),
    IN modifiedBy VARCHAR(45),
    IN articleStatus VARCHAR(15)
)
BEGIN
    UPDATE `esolutionssvc_history`.`articles`
    SET
        STATUS = articleStatus,
        MODIFIED_BY = modifiedBy,
        MODIFIED_DATE = UNIX_TIMESTAMP(),
        REVIEWED_BY = modifiedBy,
        REVIEW_DATE = UNIX_TIMESTAMP()
    WHERE ID = articleId;

    COMMIT;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `getArticleCount`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`getArticleCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`getArticleCount`(
    IN reqType VARCHAR(45)
)
BEGIN
    SELECT COUNT(*)
    FROM `esolutionssvc_history`.`articles`
    WHERE STATUS = reqType
    AND AUTHOR != requestorId;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

--
-- Definition of procedure `esolutionssvc_history`.`retrPendingArticles`
--
DROP PROCEDURE IF EXISTS `esolutionssvc_history`.`retrPendingArticles`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE PROCEDURE `esolutionssvc_history`.`retrPendingArticles`(
    IN requestorId VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY
    FROM `esolutionssvc_history`.`articles`
    WHERE STATUS IN ('NEW', 'REJECTED', 'REVIEW')
    AND AUTHOR != requestorId
    ORDER BY CREATE_DATE DESC
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$
COMMIT$$

DELIMITER ;
COMMIT;
