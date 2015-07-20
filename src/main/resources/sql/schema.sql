USE logreplay;

-- 产品信息
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (30) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- 操作记录
DROP TABLE IF EXISTS `operation_record`;
CREATE TABLE `operation_record` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `product_id` INT UNSIGNED,
  `ip` VARCHAR (25),
  `device_id` VARCHAR (100),
  `uvid` VARCHAR (100),
  `os` VARCHAR (30),
  `version` INT,
  `timestamp` BIGINT UNSIGNED,
  `page_no` INT,
  `tag_no` INT,
  `params` TEXT,
  KEY `page_no_tag_no` (`page_no`,`tag_no`)
) ENGINE = INNODB DEFAULT CHARSET = GBK;

-- 页面信息
DROP TABLE IF EXISTS `page_info`;
CREATE TABLE `page_info` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `product_id` INT UNSIGNED NOT NULL,
  `page_no` INT NOT NULL,
  `name` VARCHAR(50),
  `create_time` DATETIME,
  `update_time` DATETIME,
  UNIQUE KEY `page_no_product_id` (`page_no`, `product_id`)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

-- tag的信息
DROP TABLE IF EXISTS `tag_info`;
CREATE TABLE `tag_info` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `product_id` INT UNSIGNED NOT NULL,
  `tag_no` INT NOT NULL,
  `name` VARCHAR (50),
  `page_info_id` INT UNSIGNED,
  `page_no` INT,
  `action_id` INT UNSIGNED,
  `target_id` INT UNSIGNED,
  `comment` VARCHAR (100),
  `origin_version` INT,
  `inspect_status` TINYINT DEFAULT 0,
  `dev_inspect_status` TINYINT DEFAULT 0,
  `create_time` DATETIME,
  `update_time` DATETIME,
  UNIQUE KEY `page_info_id_tag_no_product_id` (`page_info_id`,`tag_no`, `product_id`),
  UNIQUE KEY `page_no_tag_no_product_id` (`page_no`, `tag_no`, `product_id`)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的操作
DROP TABLE IF EXISTS `tag_action`;
CREATE TABLE `tag_action` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (50) NOT NULL,
  `enabled` TINYINT (1) DEFAULT 1
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的目标
DROP TABLE IF EXISTS `tag_target`;
CREATE TABLE `tag_target` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (50) NOT NULL,
  `enabled` TINYINT (1) DEFAULT 1
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的参数
DROP TABLE IF EXISTS `tag_param`;
CREATE TABLE `tag_param` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `tag_info_id` INT UNSIGNED,
  `comment` VARCHAR (100)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- tag的参数信息
CREATE TABLE `param_info` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `tag_param_id` INT UNSIGNED,
  `name` VARCHAR (40),
  `value` VARCHAR (40),
  `description` VARCHAR (100)
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

-- 日志项校验结果
DROP TABLE IF EXISTS `inspection_record`;
CREATE TABLE inspection_record (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `product_id` INT UNSIGNED,
  `page_info_id` INT UNSIGNED,
  `tag_info_id` INT UNSIGNED,
  `valid` TINYINT (1),
  `solved` TINYINT (1),
  `submitter_id` INT UNSIGNED,
  `submitter_role_id` INT UNSIGNED,
  `solver_id` INT UNSIGNED,
  `solver_role_id` INT UNSIGNED,
  `comment` VARCHAR (200),
  `create_time` DATETIME,
  `update_time` DATETIME
) ENGINE = INNODB DEFAULT CHARSET = GBK ;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR (45) NOT NULL UNIQUE,
  `screen_name` VARCHAR (45),
  `password` VARCHAR (45),
  `create_time` DATETIME,
  `update_time` DATETIME,
  `enabled` TINYINT(1)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR (45) UNIQUE,
  `display_name` VARCHAR (45),
  `comment` VARCHAR (100)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `user_rel_role`;
CREATE TABLE `user_rel_role` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `role_id` INT UNSIGNED NOT NULL,
  UNIQUE KEY `user_rel_role` (`user_id`, `role_id`)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `target` varchar(20),
  `action` varchar(20)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `role_rel_permission`;
CREATE TABLE `role_rel_permission` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `role_id` INT UNSIGNED NOT NULL,
  `permission_id` INT UNSIGNED NOT NULL,
  UNIQUE KEY `role_rel_permission` (`role_id`, `permission_id`)
) ENGINE=INNODB DEFAULT CHARSET=GBK;

DROP TABLE IF EXISTS `image`;
CREATE TABLE `image` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `creator_id` INT UNSIGNED,
  `checksum` VARCHAR (40),
  `format` VARCHAR (10),
  `type` VARCHAR (10),
  `width` INT,
  `height` INT,
  `size` INT,
  `create_time` DATETIME
) ENGINE=INNODB DEFAULT CHARSET=GBK;

-- 用户头像
DROP TABLE IF EXISTS `avatar`;
CREATE TABLE `avatar` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT UNSIGNED,
  `image_id` INT UNSIGNED,
  `type` VARCHAR (10),
  `create_time` DATETIME
) ENGINE = INNODB DEFAULT CHARSET = GBK ;
