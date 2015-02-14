-- 操作记录
DROP TABLE IF EXISTS `operation_record`;
CREATE TABLE `operation_record` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `ip` VARCHAR (25),
  `device_id` VARCHAR (100),
  `uvid` VARCHAR (100),
  `os` VARCHAR (30),
  `version` INT (10),
  `timestamp` DATETIME
) ENGINE = INNODB DEFAULT CHARSET = GBK;

-- 页面信息
DROP TABLE IF EXISTS `page_info`;
CREATE TABLE `page_info` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  `page_no` INT(11) NOT NULL UNIQUE,
  `name` VARCHAR(50),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE=INNODB DEFAULT CHARSET=GBK;

-- tag的信息
DROP TABLE IF EXISTS `tag_info`;
CREATE TABLE `tag_info` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `tag_no` INT (11) NOT NULL,
  `name` VARCHAR (50),
  `page_info_id` BIGINT (20) NOT NULL,
  `action_id` BIGINT (20),
  `target_id` BIGINT (20),
  `comment` VARCHAR (100),
  `create_time` DATETIME,
  `update_time` DATETIME,
  UNIQUE KEY `page_info_id_tag_no` (`page_info_id`,`tag_no`)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的操作
DROP TABLE IF EXISTS `tag_action`;
CREATE TABLE `tag_action` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (50) NOT NULL,
  `enabled` TINYINT (1) DEFAULT 1
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的目标
DROP TABLE IF EXISTS `tag_target`;
CREATE TABLE `tag_target` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (50) NOT NULL,
  `enabled` TINYINT (1) DEFAULT 1
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的参数
DROP TABLE IF EXISTS `tag_param`;
CREATE TABLE `tag_param` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `tag_info_id` BIGINT (20),
  `comment` VARCHAR (100)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的参数信息
CREATE TABLE `param_info` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `tag_param_id` BIGINT (20),
  `name` VARCHAR (40),
  `value` VARCHAR (40),
  `description` VARCHAR (100)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(45) UNIQUE,
  `password` VARCHAR(45),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE=INNODB DEFAULT CHARSET=GBK;