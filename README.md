# 客户端日志埋点校验系统

### 介绍
- 本系统用于管理手机客户端日志埋点信息，并对埋点日志进行直观有效的测试和验证。

### 主要功能

- **管理用户:** 包括创建用户，登录，修改角色和密码等。

- **埋点信息管理:** 包括创建、修改**页面信息**、**操作项信息**、**操作参数信息**等。以上埋点信息均按不同产品类型进行区分，可通过右上角下拉框切换产品类型。

- **埋点信息校验:** 包括**实时校验**，查询和处理**校验结果**，以及操作项的**校验状态查询**等功能。

### 技术选型

- **前端框架:**&ensp;&ensp;jQuery, Bootstrap3, Sea.js

- **后端框架:**&ensp;&ensp;Spring Boot, Spring Framework, Shiro

- **数据库:**&ensp;&ensp;&ensp;&ensp;MySQL

### 开发环境运行方法

 &ensp;&ensp;**1.下载项目**  `git clone https://github.com/yangziwen/logreplay.git`

 &ensp;&ensp;**2.初始化数据库**
 
```
create database logreplay default charset utf8;
grant all on logreplay.* to 'mobile'@localhost identified by 'mobile';
flush privileges;
```
&ensp;&ensp;此后在logreplay数据库中执行src/main/resources/sql路径下的schema.sql和data.sql中的sql
```
use logreplay;
set autocommit=0;
source src/main/resources/sql/schema.sql; commit;
source src/main/resources/sql/data.sql; commit;
set autocommit=1;
```

 &ensp;&ensp;**3.运行项目**  `mvn spring-boot:run`

 &ensp;&ensp;**4.访问项目** [http://localhost:8075](http://localhost:8075) ( 用户名:admin / 密码: 1234 )

### 生产环境运行方法
 &ensp;&ensp;**1.打包项目** `mvn clean package`

 &ensp;&ensp;**2.运行项目** `java -jar -Dspring.config.location=${your_application_properties_file} logreplay.war`

 &ensp;&ensp;注意，虽然项目的打包产出是war包，但是只能基于内嵌tomcat直接运行，无法部署到独立的tomcat中