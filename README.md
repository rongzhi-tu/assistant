## 目录结构

```
|-- logmirror
    |-- package                                可以被打进 zip 包的额外文件，具体配置在 bbp-assembly.xml 中
    |   |-- db                                 数据库脚本
    |   |-- assembly.xml                       发布包配置
    |-- src                                    后端源码
    |   |-- main
    |   |   |-- java
    |   |   |   |-- com                         com 和 bsoft 两级目录对应 spring-service.xml 中的 packagePrefix 属性
    |   |   |       |-- bsoft
    |   |   |           |-- logmirror           logmirror 对应 spring-service.xml 中的 name 属性。目录名与 name 属性同步修改
    |   |   |-- resources
    |   |   |   |-- index.js
    |   |   |   |-- log4j.properties            
    |   |   |   |-- spring-account.xml          从 BBP 获取用户、角色、租住信息的配置。必须配置
    |   |   |   |-- spring-dictionary.xml       从 BBP 获取字典信息的配置。必须配置
    |   |   |   |-- spring-dns.xml              字典广播配置。当把本项目的功能模块挂载到 BBP 项目上显示时起作用。建议配置
    |   |   |   |-- spring-hibernate.xml        数据库配置。默认未启用。可以在 spring.xml 开启
    |   |   |   |-- spring-module.xml           从 BBP 获取功能模块信息的配置。必须配置
    |   |   |   |-- spring-mvc.xml              
    |   |   |   |-- spring-param.xml            从 BBP 获取参数信息的配置。必须配置
    |   |   |   |-- spring-service.xml          RPC 服务配置
    |   |   |   |-- spring-ws.xml               Web Service 配置
    |   |   |   |-- spring.xml                 
    |   |   |   |-- package                     分环境配置 spring
    |   |   |   |   |-- development.properties  开发环境配置。默认
    |   |   |   |   |-- production.properties   生产环境配置
    |   |   |   |-- repository                  上传文件默认路径。不要删除
    |   |   |   |   |-- placeholder
    |   |   |   |-- web-vue2                    前端界面。可以自定义修改
    |   |   |       |-- ssdev
    |   |   |           |-- ux
    |   |   |               |-- login           登陆界面
    |   |   |               |-- portal          个人中心界面
    |   |   |               |-- profile         换肤界面
    |   |   |-- script                          前端源码
    |   |   |   |-- com                         com 和 bsoft 两级目录对应 spring-service.xml 中的 packagePrefix 属性
    |   |   |       |-- bsoft
    |   |   |           |-- logmirror           logmirror 对应 spring-service.xml 中的 name 属性。目录名与 name 属性同步修改
    |   |-- test                                单元测试
    |       |-- java
    |           |-- placeholder
```

## 配置文件

提供两套环境的配置：开发环境（默认） `development.properties` 和生产环境 `production.properties`。

Intellij IDEA 开发的时候直接修改 `development.properties` 即可。会自动替换 spring 中的占位符。

| 属性 |说明  |
| --- | --- |
| zookeeper.server | zookeeper 地址 |
| rpc.server | rpc 服务发布地址 |
| db.url | 数据库连接地址 |
| db.username | 数据库用户名 |
| db.password | 数据库密码 |
| db.dialect | 数据库方言 |
| db.validationQuery | oracle 配置 SELECT 'x' FROM DUAL，MySQL 配置 SELECT 'x' |

## 打包说明

如果需要打 war 包放到生产环境服务器运行，执行

```bash
mvn clean package -Dassembly.skipAssembly=true
```

如果需要打一个包括 war 包和数据库脚本的 zip 包，执行

```bash
mvn clean package
```

运行之前根据服务器环境修改 war 包中的 `alpine.properties`

## Docker 启动

生成 war 包

```bash
mvn clean package -Dassembly.skipAssembly=true
```

生成 image

```bash
docker build --no-cache -t bbp-alpine:1.0 .
```

启动 container

```bash
docker run -p 8081:8080 --name bbp-alpine bbp-alpine:1.0
```

启动时可以指定以下参数配置

| 参数             | 说明                                                         |
| ---------------- | ------------------------------------------------------------ |
| zookeeper.server | zookeeper 连接地址。`0.0.0.0:2181`                           |
| rpc.server       | rpc 服务发布地址。`0.0.0.0:9010`                             |
| db.url           | mysql 数据库地址。`jdbc:mysql://localhost/your-db-name?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false` |
| db.username      | mysql 数据库用户名。`root`                                   |
| db.password      | mysql 数据库密码。`bsoft`                                    |
| file.server.host | BBP 所在的服务器地址。`0.0.0.0`                              |
| file.server.port | BBP 所在的 Tomcat 端口号。`8080`                             |

例如：

```bash
docker run -p 8081:8080 --name bbp-alpine -e CATALINA_OPTS="-Dzookeeper.server=10.0.22.5:2181" bbp-alpine:1.0
```