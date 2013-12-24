-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version    5.0.51b-community-nt-log
--
-- Create schema esolutions
--
CREATE DATABASE IF NOT EXISTS esolutionssvc;
COMMIT;

USE esolutionssvc;

SOURCE ./eSolutions/eSolutionsService.articles.sql;
SOURCE ./eSolutions/eSolutionsService.dns_service.sql;
SOURCE ./eSolutions/eSolutionsService.installed_applications.sql;
SOURCE ./eSolutions/eSolutionsService.installed_systems.sql;
SOURCE ./eSolutions/eSolutionsService.service_datacenters.sql;
SOURCE ./eSolutions/eSolutionsService.service_messages.sql;
SOURCE ./eSolutions/eSolutionsService.service_platforms.sql;

COMMIT;

--
-- add privileges
--
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON esolutionssvc.* TO 'appuser'@'localhost' IDENTIFIED BY PASSWORD '*ED66694310AF846C68C9FC3D430B30594837998D' REQUIRE SSL;
GRANT SELECT ON `mysql`.`proc` TO 'appuser'@'localhost';

FLUSH PRIVILEGES;
COMMIT;
