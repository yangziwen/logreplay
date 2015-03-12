-- 操作记录
DROP TABLE IF EXISTS `operation_record`;
CREATE TABLE `operation_record` (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `ip` VARCHAR (25),
  `device_id` VARCHAR (100),
  `uvid` VARCHAR (100),
  `os` VARCHAR (30),
  `version` INT (10),
  `timestamp` BIGINT(15),
  `page_no` INT(11),
  `tag_no` INT(11),
  `params` TEXT,
  KEY `page_no_tag_no` (`page_no`,`tag_no`)
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
  `page_no` int(11),
  `action_id` BIGINT (20),
  `target_id` BIGINT (20),
  `comment` VARCHAR (100),
  `origin_version` INT(11),
  `inspect_status` int(2),
  `create_time` DATETIME,
  `update_time` DATETIME,
  UNIQUE KEY `page_info_id_tag_no` (`page_info_id`,`tag_no`),
  UNIQUE KEY `page_no_tag_no` (`page_no`, `tag_no`)
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

-- 日志项校验结果
CREATE TABLE inspection_record (
  `id` BIGINT (20) PRIMARY KEY AUTO_INCREMENT,
  `page_info_id` BIGINT (20),
  `tag_info_id` BIGINT (20),
  `valid` TINYINT (1),
  `solved` TINYINT (1),
  `submitter_id` BIGINT (20),
  `solver_id` BIGINT (20),
  `comment` VARCHAR (200),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL UNIQUE,
  `screen_name` varchar(45),
  `password` VARCHAR(45),
  `create_time` DATETIME,
  `update_time` DATETIME,
  `enabled` TINYINT(1)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(45) UNIQUE,
  `comment` varchar(100)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `user_rel_role`;
CREATE TABLE `user_rel_role` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `role_id` BIGINT(20) NOT NULL,
  UNIQUE KEY `user_rel_role` (`user_id`, `role_id`)
) ENGINE=INNODB DEFAULT CHARSET=GBK;