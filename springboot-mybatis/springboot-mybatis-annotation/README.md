# SpringBoot整合Mybatis、Mysql
spring boot集成Mybatis有两种方式，一种是注解版，不需要xml配置文件，
另一种是xml配置版
### 注解版
#### 第一步 引入依赖包
在pom.xml添加：
```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.17</version>
</dependency>

```
最好指明Mysql版本，实际根据项目运行环境安装的Mysql版本为准，
笔者使用的Mysql版本为8.0.17版本。
#### 第二步 配置application.properties
```properties
mybatis.type-aliases-package=com.neo.model

spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```


