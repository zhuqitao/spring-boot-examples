# SoringBoot、Mybatis、Druid多数据源JTA分布式事务实现
随着需求不断的增加，业务逐渐的增强，项目越来越复杂，有时候单数据源无法满足我们的开发，
就需要多个数据源来实现业务需求。本章内容就是搭建一个多数据源分布式事务配置的服务。

下面先来弄清楚几个概念。
### 什么是Druid
官方原文: 阿里巴巴数据库事业部出品，为监控而生的数据库连接池。Druid是Java语言中最好的数据库连接池。Druid能够提供强大的监控和扩展功能。

[Druid官方github地址](https://github.com/alibaba/druid)

### 为什么需要分布式事务
Java事务API（Java Transaction API，简称JTA ） 是一个Java企业版 的应用程序接口，在Java环境中，允许完成跨越多个XA资源的分布式事务。JTA是在Java社区过程下制定的规范，编号JSR 907。

与JDBC的区别，通俗的讲，JTA是多库的事务 JDBC是单库的事务，也就是说JTA可以管理多个数据源。

比如一个货物管理的项目连接了两个数据源，一个用来管理出售订单source1，一个用来管理库存余量source2，
客户下单就要在管理出售订单的库中添加一条订单数据，同时也要设置管理库存余量的库，正常来讲，
这两个操作要么一起成功，要么一起失败，假如现在下单成功了，但是在修改库存余量时发生了错误未能成功修改数据源，
就需要管理出售订单的数据源回滚，而正好JTA可以帮我们管理这些数据源。

### 开始配置
#### 创建两个数据库
创建两个数据库test1、test2
#### 添加依赖
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.46</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```
**注意**
mysql-connector-java使用的是5.1.46版本，JTA暂时不支持Mysql8

#### application.yml多数据源配置
```yaml
datasource:
    druid:
      first:
        unique-resource-name: firstDataSource
        xa-data-source-class-name: com.alibaba.druid.pool.xa.DruidXADataSource
        driver-class-name: com.mysql.jdbc.Driver
        # 连接池配置
        initial-size: 5
        min-idle: 5
        max-active: 20
        # 连接等待超时时间
        max-wait: 30000
        # 配置检测可以关闭的空闲连接间隔时间
        time-between-eviction-runs-millis: 60000
        # 配置连接在池中的最小生存时间
        min-evictable-idle-time-millis: 300000
        validation-query: select '1' from dual
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false
        # 打开PSCache，并且指定每个连接上PSCache的大小
        pool-prepared-statements: true
        max-open-prepared-statements: 20
        max-pool-prepared-statement-per-connection-size: 20
        # 配置监控统计拦截的filters, 去掉后监控界面sql无法统计, 'wall'用于防火墙
        filters: stat,wall
        aop-patterns: com.springboot.servie.*
        xa-properties:
          url: jdbc:mysql://localhost:3306/test1?useUnicode=true&characterEncoding=UTF-8&useSSL=false
          username: root
          password: zhuqitao123
      second:
        unique-resource-name: secondDataSource
        xa-data-source-class-name: com.alibaba.druid.pool.xa.DruidXADataSource
        driver-class-name: com.mysql.jdbc.Driver
        # 连接池配置
        initial-size: 5
        min-idle: 5
        max-active: 20
        # 连接等待超时时间
        max-wait: 30000
        # 配置检测可以关闭的空闲连接间隔时间
        time-between-eviction-runs-millis: 60000
        # 配置连接在池中的最小生存时间
        min-evictable-idle-time-millis: 300000
        validation-query: select '1' from dual
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false
        # 打开PSCache，并且指定每个连接上PSCache的大小
        pool-prepared-statements: true
        max-open-prepared-statements: 20
        max-pool-prepared-statement-per-connection-size: 20
        # 配置监控统计拦截的filters, 去掉后监控界面sql无法统计, 'wall'用于防火墙
        filters: stat,wall
        # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔
        aop-patterns: com.springboot.servie.*
        xa-properties:
          url: jdbc:mysql://localhost:3306/test2?useUnicode=true&characterEncoding=UTF-8&useSSL=false
          username: root
          password: zhuqitao123
```


#### 数据源配置类
FirstDataSourceConfig：
多个数据源需要指定主数据源，使用@Primary注解指定主数据源。
```java
package com.love.example.Config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.love.example.mapper.first", sqlSessionTemplateRef="firstSqlSessionTemplate")
public class FirstDataSourceConfig {
    @Bean(name = "firstDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid.first")
    public DataSource firstDataSource(Environment env) {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "firstTransactionManager")
    @Primary
    public DataSourceTransactionManager firstTransactionManager(@Qualifier("firstDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "firstSqlSessionFactory")
    @Primary
    public SqlSessionFactory firstSqlSessionFactory(@Qualifier("firstDataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/first/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean(name = "firstSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate firstSqlSessionTemplate(@Qualifier("firstSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Primary
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        //控制台管理用户，进入druid后台(localhost:{port}/druid)需要登录
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        return servletRegistrationBean;
    }

}
```

SecondDataSourceConfig：
```java
package com.love.example.Config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.love.example.mapper.second", sqlSessionTemplateRef="secondSqlSessionTemplate")
public class SecondDataSourceConfig {
    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.second")
    public DataSource firstDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "secondTransactionManager")
    public DataSourceTransactionManager secondTransactionManager(@Qualifier("secondDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory secondSqlSessionFactory(@Qualifier("secondDataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/second/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean(name = "secondSqlSessionTemplate")
    public SqlSessionTemplate firstSqlSessionTemplate(@Qualifier("secondSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

#### 事务管理配置

TransactionManagerConfig：
```java
package com.love.example.Config;

import org.springframework.context.annotation.Bean;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
public class TransactionManagerConfig {
    @Bean
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean
    public TransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean(name = "transactionManager")
    @DependsOn({ "userTransaction", "atomikosTransactionManager" })
    public PlatformTransactionManager transactionManager() throws Throwable {
        JtaTransactionManager manager = new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
        return manager;
    }
}

```

#### 创建model
Model实体类

User：
```java
package com.love.example.model;

public class User {
    private Long id;
    private String userName;
    private String passWord;

    public User(){
        super();
    }
    public User(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return passWord;
    }

    public void setPassword(String password) {
        this.passWord = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + passWord + '\'' +
                '}';
    }
}
```

#### 分别在不同的目录下创建mapper及SQL映射xml文件
firstMapper：
```java
package com.love.example.mapper.first;

import com.love.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface FirstMapper {
    User getUser(Long id);
    int addUser(User user);
}
```
创建firstMapper的SQL映射xml文件，注意namespace指向FirstMapper

firstMapper.xml：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.love.example.mapper.first.FirstMapper" >
    <resultMap id="BaseResultMap" type="com.love.example.model.User" >
        <id column="id" property="id" jdbcType="BIGINT" />
        <result column="user_name" property="userName" jdbcType="VARCHAR" />
        <result column="password" property="passWord" jdbcType="VARCHAR" />
    </resultMap>

    <sql id="Base_Column_List" >
        id, user_name, password
    </sql>

    <select id="getUser" parameterType="java.lang.Long" resultMap="BaseResultMap" >
        SELECT
        <include refid="Base_Column_List" />
        FROM users
        WHERE id = #{id}
    </select>

    <insert id="addUser" parameterType="com.love.example.model.User" >
       INSERT INTO
       		users
       		(user_name,password)
       	VALUES
       		(#{userName}, #{passWord})
    </insert>

</mapper>
```

secondMapper
```java
package com.love.example.mapper.second;

import com.love.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SecondMapper {
    User getUser(Long id);
    int addUser(User user);
}
```

创建secondMapper的SQL映射xml文件，注意namespace指向SecondMapper

secondMapper.xml：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.love.example.mapper.second.SecondMapper" >
    <resultMap id="BaseResultMap" type="com.love.example.model.User" >
        <id column="id" property="id" jdbcType="BIGINT" />
        <result column="user_name" property="userName" jdbcType="VARCHAR" />
        <result column="password" property="passWord" jdbcType="VARCHAR" />
    </resultMap>

    <sql id="Base_Column_List" >
        id, user_name, password
    </sql>

    <select id="getUser" parameterType="java.lang.Long" resultMap="BaseResultMap" >
        SELECT
        <include refid="Base_Column_List" />
        FROM users
        WHERE id = #{id}
    </select>

    <insert id="addUser" parameterType="com.love.example.model.User" >
       INSERT INTO
       		users
       		(user_name,password)
       	VALUES
       		(#{userName}, #{passWord})
    </insert>

</mapper>
```

#### Service层测试
定义service接口

UserService：
```java
package com.love.example.Service;

import com.love.example.model.User;

public interface UserService {
    User getFirstUser(Long id);
    void addFirstUser(User user);
    User getSecondUser(Long id);
    void addSecondUser(User user);

    void addAllUser(User user);
}
```
service接口实现类

UserServiceTemplate：
```java
package com.love.example.Service.template;

import com.love.example.Service.UserService;
import com.love.example.mapper.first.FirstMapper;
import com.love.example.mapper.second.SecondMapper;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("UserService")
@Transactional("transactionManager")
public class UserServiceTemplate implements UserService {
    @Autowired
    private FirstMapper firstMapper;

    @Autowired
    private SecondMapper secondMapper;

    @Override
    public User getFirstUser(Long id) {
        return this.firstMapper.getUser(id);
    }

    @Override
    public void addFirstUser(User user) {
        this.firstMapper.addUser(user);
    }

    @Override
    public User getSecondUser(Long id) {
        return this.secondMapper.getUser(id);
    }

    @Override
    public void addSecondUser(User user) {
        this.secondMapper.addUser(user);
    }

    @Override
    public void addAllUser(User user) {
        this.firstMapper.addUser(user);
        // 创造错误
        int a = 2/0;
        this.secondMapper.addUser(user);
    }
}

```
UserServiceTemplate使用@Transactional("transactionManager")注入分布式事务管理。

注意在addAllUser方法中添加了一行"int a = 2/0;"用来模拟开发中异常现象。

如果在UserServiceTemplate中使用了@Transactional("transactionManager")
注入分布式管理器，调用addAllUser方法时，两个数据库都不会插入数据，如果去除@Transactional("transactionManager")，
再调用addAllUser方法时，会发现first库成功插入了数据，second没有插入数据。


#### 编写Controller

UserController：
```java
package com.love.example.Controller;

import com.love.example.Service.UserService;
import com.love.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getfirstuser")
    public User getFirstUser(Long id) {
        return this.userService.getFirstUser(id);
    }
    @GetMapping("/addfirstuser")
    public String addFirstUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addFirstUser(user);
        return "true";
    }

    @GetMapping("/getseconduser")
    public User getSecondUser(Long id) {
        return this.userService.getSecondUser(id);
    }
    @GetMapping("/addseconduser")
    public String addSecondUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addSecondUser(user);
        return "true";
    }

    @GetMapping("/addalluser")
    public String addAllUser(String userName, String password) {
        User user = new User(userName, password);
        this.userService.addAllUser(user);
        return "true";
    }

}
```

#### 测试
恭喜坚持到了最后一步，到此就完成了配置，启动项目，在浏览器访问[http://localhost:8083/addalluser?userName=test_name&password=123456](http://localhost:8083/addalluser?userName=test_name&password=123456),
会抛出异常，查看数据库会发现test1库和test2库两个数据源都没有插入数据，如果在UserServiceTemplate中去除@Transactional("transactionManager")，
刷新浏览器重新发出请求，同样会抛出异常，查看两个数据源first库成功插入了数据，second库没有插入数据。

如果出现上述效果，说明配置成功了。