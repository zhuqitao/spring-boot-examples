# Spring Boot集成Mybatis
springboot集成Mybatis和MySQL
### 添加依赖
```
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.17</version>
</dependency>
```
最好指定mysql版本，根据项目运行环境安装的MySQL版本填写对应的版本
### 配置application.properties
```
mybatis.type-aliases-package=com.love.example.model

spring.datasource.url=jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
***注意：***
Mysql6.x之前的版本需要指定spring.datasource.driver-class-name=com.mysql.jdbc.Driver

Mysql6.x及以上版本需要指定spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

这就是为什么添加mysql依赖时为什么要指定版本的原因了，开始的版本mysql驱动器使用的是com.mysql.jdbc.Driver，之后的版本换成了新的驱动器 com.mysql.cj.jdbc.Driver

### 添加mapper包配置
- 使用@Mapper注解

@Mapper指明这是一个Mapper， Mybatis 会有一个拦截器，会自动的把 @Mapper 注解的接口生成动态代理类
```
@Mapper
public interface MyMapper {
    ...
}
```
- 使用@MapperScan注解
@Mapper需要每个mapper类都添加@Mapper注解比较繁琐，为了方便@MapperScan就出现了，@MapperScan配置一个或多个包路径，自动的扫描这些包路径下的类，自动的为它们生成代理类。

在启动类中添加@MapperScan
```
@SpringBootApplication
@MapperScan("com.love.example.mapper")
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

### 配置Mybatis两种方式： 使用注解和使用xml配置文件

配置Mybatis有两种方式，一种是使用注解，就是所有的都由注解来搞定，还有一种是使用xml配置文件。

注解方式特点：
- 注解方式简洁，不需要写额外大量的xml配置文件
- 注解方式灵活性低，但后期可维护型差
- 注解方式对于小型项目可以提高开发效率
- 注解相对于XML的另一个好处是类型安全的，XML只能在运行期才能发现问题。

xml方式特点
- XML配置起来有时候冗长
- 灵活性和可扩展性高，后期维护方便
两种方式都有各自适用的场景，还有一种方案是约定大于配置，在某些场景这个方案可能是最优的，但无法满足非常复杂的业务场景。由于企业业务变化需求大，方便维护起见，现在企业大部分使用的是xml方式。

#### 注解方式：
注解方式其实就是借助@Select、@Results、@Result、@Insert等注解，把操作数据库的方法和mapper接口写在一块实现对数据库的增删改查等操作。
##### 一、定义mapper接口
```
@Component
@Mapper
public interface MyMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({

            @Result(property = "passWord",  column = "password"),
            @Result(property = "userName", column = "user_name")
    })
    User getUser(Long id);

    @Insert("INSERT INTO users(user_name,password) VALUES(#{userName}, #{passWord})")
    int addUser(User user);

    @Update("UPDATE users SET user_name=#{userName},password=#{passWord}, WHERE id =#{id}")
    int setUser(User user);

    @Delete("DELETE FROM users WHERE id =#{id}")
    int removeUser(Long id);

}
```
了解@Select、@Results、@Result、@Insert等注解使用方法请查看[这里](https://mybatis.org/mybatis-3/zh/java-api.html)

##### 二、使用Mapper
- 定义service接口
```
public interface UserService {
    User getUser(Long id);
    int addUser(User user);
    int setUser(User user);
    int removeUser(Long id);
}
```
- 实现service接口
```
@Service("UserService")
public class UserServiceTemplate implements UserService {
    @Autowired
    private MyMapper myMapper;

    @Override
    public User getUser(Long id) {
        return this.myMapper.getUser(id);
    }
    @Override
    public int addUser(User user) {
        return this.myMapper.addUser(user);
    }
    @Override
    public int setUser(User user) {
        return this.myMapper.setUser(user);
    }
    @Override
    public int removeUser(Long id) {
        return this.myMapper.removeUser(id);
    }
}
```
- 定义controller
```
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/getUser")
    public User getUser(Long id) {
        return this.userService.getUser(id);
    }
    @GetMapping("/addUser")
    public int addUser(User user) {
        User user1 = new User("test", "123456");
        return this.userService.addUser(user1);
    }

    @PostMapping("/setUser")
    public int setUser(User user) {
        return this.userService.setUser(user);
    }
    @PostMapping("/removeUser")
    public int removeUser(Long id) {
        return this.userService.removeUser(id);
    }
}
```

到此项目就配置完成了，配置本地MySQL数据库，新建一个users表，启动项目，在浏览器中输入[http://localhost:8080/addUser](http://localhost:8080/addUser)，就可以添加一条User数据，刷新自己本地数据库，查看是否添加一条数据，然后访问[http://localhost:8080/getUser?id=1](http://localhost:8080/getUser?id=1)(id为刚才添加的User的id)，如果一切正常显示说明Mybatis配置成功了。

#### xml方式：
mapper只定义接口方法，并没有对数据库的操作，对数据库的操作写在xml文件，根据mapper的方法名与xml文件中标签的id匹配。

和注解方式的不同主要也是体现在这里，注解是把数据库操作和mapper接口写在一起。
##### 一、 定义mapper和xml映射文件
MyMapper: 
```
public interface MyMapper {
    User getUser(Long id);
    int addUser(User user);
    int setUser(User user);
    int removeUser(Long id);
}
```
在resources下创建mybatis/mapper目录，并创建MyMapper.xml文件
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.love.example.mapper.MyMapper" >
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
       		(userName,passWord)
       	VALUES
       		(#{userName}, #{passWord})
    </insert>
    <update id="setUser" parameterType="com.love.example.model.User" >
        UPDATE
        users
        SET
        <if test="userName != null">userName = #{userName},</if>
        <if test="passWord != null">passWord = #{passWord},</if>
        WHERE
        id = #{id}
    </update>
    <delete id="removeUser" parameterType="java.lang.Long" >
       DELETE FROM
       		 users
       WHERE
       		 id =#{id}
    </delete>
</mapper>
```
##### 二、 application.properties配置
现在MyMapper.java接口文件在main/java/com/love/example/mapper/目录下，而MyMapper.xml文件在resources/mybatis/mapper目录下，怎么让这两个文件一起工作呢？

这就需要在application.properties添加以下配置
```
mybatis.config-location=classpath:mybatis/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
```
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml是为了让程序去扫描该目录下的所有xml文件，需要注意Mapper接口文件和xml文件名字要相同，他们是根据相同名字匹配。

到此配置完成，配置本地数据库，启动项目，和上面看到一样的效果说明Mybatis的xml配置方式配置成功。

### 结构分析
该部分针对从事前端不熟悉java的同学，可能对这个结构不是很熟悉，搞不清Mapper层、service层、controller层都是干嘛的，用一张图梳理他们之间的关系
![image]()

- Controller层
  在springMVC中Controller层负责具体的业务模块流程的控制，主要是处理客户端发送到服务端的请求，调用service层定义的方法。
- Service层
  Service层主要负责业务模块的逻辑应用设计。先设计接口，再设计接口的实现类。
- Mapper层
  也叫dao层，对数据库进行数据持久化操作，他的方法语句是直接针对数据库操作的，主要实现一些增删改查操作。
