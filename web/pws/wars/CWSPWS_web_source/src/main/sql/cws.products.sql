/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cws;

--
-- Definition of table `cws`.`products`
-- DATA TABLE
--
DROP TABLE IF EXISTS `cws`.`products`;
CREATE TABLE `cws`.`products` (
	`product_id` VARCHAR(128) NOT NULL,
	`product_name` VARCHAR(128) NOT NULL,
	`product_desc` TEXT NOT NULL,
	`product_price` VARCHAR(20) NOT NULL DEFAULT '0.00',
	PRIMARY KEY (`product_id`),
	FULLTEXT KEY `TK_search_products` (`product_id`, `product_name`, `product_price`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cws`.`products`
--
/*!40000 ALTER TABLE `cws`.`products` DISABLE KEYS */;
/*!40000 ALTER TABLE `cws`.`products` ENABLE KEYS */;

COMMIT;

--
-- Definition of procedure `cws`.`getProductList`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`getProductList`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`getProductList`(
)
BEGIN
    SELECT
        `product_id`, `product_name`
    FROM
        `cws`.`products`
    ORDER BY product_name ASC;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `cws`.`getProductById`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cws`.`getProductById`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cws`.`getProductById`(
    IN productId VARCHAR(128)
)
BEGIN
    SELECT
        `product_id`, `product_name`
    FROM
        `cws`.`products`
    WHERE `product_id` = productId;
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