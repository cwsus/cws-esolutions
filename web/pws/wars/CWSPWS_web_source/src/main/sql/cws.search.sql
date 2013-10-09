/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cws;

--
-- Definition of table `cws`.`site_search`
--
DROP TABLE IF EXISTS `cws`.`site_search`;
CREATE TABLE `cws`.`site_search` (
  `search_terms` varchar(100) NOT NULL default '',
  `page_title` varchar(45) NOT NULL default '',
  `page_url` varchar(45) NOT NULL default '',
  `page_desc` varchar(45) NOT NULL default '',
  `page_lang` varchar(45) NOT NULL default '',
  FULLTEXT KEY `search` (`search_terms`,`page_title`,`page_url`,`page_desc`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cws`.`site_search`
--
/*!40000 ALTER TABLE `cws`.`site_search` DISABLE KEYS */;
/*!40000 ALTER TABLE `cws`.`site_search` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cws`.`search_site`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`search_site`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`search_site`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        page_url, page_title, page_desc,
    MATCH (search_terms, page_title, page_url, page_desc)
    AGAINST (searchTerms WITH QUERY EXPANSION) AS score
    FROM `cws`.`site_search`
    WHERE MATCH (search_terms, page_title, page_url, page_desc)
    AGAINST (searchTerms IN BOOLEAN MODE);
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