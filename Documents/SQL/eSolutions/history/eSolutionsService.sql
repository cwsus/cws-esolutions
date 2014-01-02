-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version    5.0.51b-community-nt-log
--
-- Create schema esolutions
--
CREATE DATABASE IF NOT EXISTS esolutionssvc_history;
COMMIT;

USE esolutionssvc_history;

SOURCE ./eSolutionsService.articles.sql;
SOURCE ./eSolutionsService.dns_service.sql;
SOURCE ./eSolutionsService.installed_applications.sql;
SOURCE ./eSolutionsService.installed_systems.sql;
SOURCE ./eSolutionsService.services.sql;
SOURCE ./eSolutionsService.service_messages.sql;

COMMIT;

--
-- add privileges
--
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON esolutionssvc_history.* TO 'appuser'@'localhost' IDENTIFIED BY PASSWORD '*ED66694310AF846C68C9FC3D430B30594837998D';
GRANT SELECT ON `mysql`.`proc` TO 'appuser'@'localhost';

FLUSH PRIVILEGES;
COMMIT;
